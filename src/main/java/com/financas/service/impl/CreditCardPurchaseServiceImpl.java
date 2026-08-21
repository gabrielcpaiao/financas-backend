package com.financas.service.impl;

import com.financas.domain.*;
import com.financas.domain.enums.CreditCardInstallmentStatus;
import com.financas.domain.enums.CreditCardInvoiceStatus;
import com.financas.domain.enums.ExpenseType;
import com.financas.domain.enums.PaymentMethod;
import com.financas.domain.enums.TransactionType;
import com.financas.dto.request.CreditCardPurchaseRequest;
import com.financas.dto.response.CreditCardInstallmentResponse;
import com.financas.dto.response.CreditCardPurchaseResponse;
import com.financas.exception.ResourceNotFoundException;
import com.financas.repository.*;
import com.financas.service.CreditCardPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditCardPurchaseServiceImpl implements CreditCardPurchaseService {

    private final CreditCardRepository creditCardRepository;
    private final CreditCardPurchaseRepository purchaseRepository;
    private final CreditCardInstallmentRepository installmentRepository;
    private final CreditCardInvoiceRepository invoiceRepository;
    private final CategoryRepository categoryRepository;
    private final FinancialTransactionRepository financialTransactionRepository;

    @Override
    public List<CreditCardPurchaseResponse> listByCard(Long userId, Long creditCardId) {
        // Garante ownership do cartão antes de listar (mesmo padrão de
        // findOwnedOrThrow usado no resto do projeto).
        findOwnedCardOrThrow(userId, creditCardId);

        return purchaseRepository.findByUserIdAndCreditCardIdOrderByPurchaseDateDescIdDesc(userId, creditCardId).stream()
                .map(purchase -> toPurchaseResponse(purchase,
                        installmentRepository.findByCreditCardPurchaseIdOrderByInstallmentNumber(purchase.getId())))
                .toList();
    }

    @Override
    @Transactional
    public CreditCardPurchaseResponse create(Long userId, Long creditCardId, CreditCardPurchaseRequest request) {
        CreditCard card = findOwnedCardOrThrow(userId, creditCardId);

        categoryRepository.findByIdAndUserId(request.categoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + request.categoryId()));

        CreditCardPurchase purchase = CreditCardPurchase.builder()
                .userId(userId)
                .creditCardId(creditCardId)
                .categoryId(request.categoryId())
                .description(request.description())
                .store(request.store())
                .totalAmount(request.totalAmount())
                .installmentCount(request.installmentCount())
                .notes(request.notes())
                .purchaseDate(request.purchaseDate())
                .build();
        purchase = purchaseRepository.save(purchase);

        List<CreditCardInstallment> installments = generateInstallmentsAndTransactions(userId, card, purchase, request);

        return toPurchaseResponse(purchase, installments);
    }

    /**
     * Regra de atribuição de parcela -> fatura: se o dia da compra for depois
     * do fechamento do cartão, a 1ª parcela cai na fatura do mês seguinte;
     * senão, cai na fatura do mês corrente. As parcelas seguintes avançam
     * 1 mês cada. Cada parcela gera um financial_transaction (EXPENSE +
     * CREDIT_CARD) datado no primeiro dia do mês de referência da fatura,
     * o que alimenta a vw_monthly_expense_summary no mês correto.
     */
    private List<CreditCardInstallment> generateInstallmentsAndTransactions(Long userId, CreditCard card,
                                                                            CreditCardPurchase purchase, CreditCardPurchaseRequest request) {

        int installmentCount = request.installmentCount();
        ExpenseType expenseType = request.expenseType() != null ? request.expenseType() : ExpenseType.VARIABLE;

        BigDecimal baseAmount = request.totalAmount()
                .divide(BigDecimal.valueOf(installmentCount), 2, RoundingMode.DOWN);
        BigDecimal remainder = request.totalAmount()
                .subtract(baseAmount.multiply(BigDecimal.valueOf(installmentCount)));

        LocalDate billingMonth = billingMonthFor(request.purchaseDate(), card.getClosingDay());

        List<CreditCardInstallment> installments = new ArrayList<>();

        for (int i = 1; i <= installmentCount; i++) {
            LocalDate referenceMonth = billingMonth.plusMonths(i - 1L);
            CreditCardInvoice invoice = getOrCreateInvoice(card, referenceMonth);

            BigDecimal amount = (i == installmentCount) ? baseAmount.add(remainder) : baseAmount;

            CreditCardInstallment installment = CreditCardInstallment.builder()
                    .creditCardPurchaseId(purchase.getId())
                    .invoiceId(invoice.getId())
                    .installmentNumber(i)
                    .amount(amount)
                    .dueDate(invoice.getDueDate())
                    .status(CreditCardInstallmentStatus.PENDING)
                    .build();
            installment = installmentRepository.save(installment);
            installments.add(installment);

            String description = installmentCount > 1
                    ? request.description() + " (" + i + "/" + installmentCount + ")"
                    : request.description();

            FinancialTransaction transaction = FinancialTransaction.builder()
                    .userId(userId)
                    .sourceAccountId(null)
                    .destinationAccountId(null)
                    .categoryId(request.categoryId())
                    .type(TransactionType.EXPENSE)
                    .transactionDate(referenceMonth)
                    .description(description)
                    .amount(amount)
                    .expenseType(expenseType)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .notes(request.notes())
                    .creditCardInstallmentId(installment.getId())
                    .build();
            financialTransactionRepository.save(transaction);
        }

        return installments;
    }

    private LocalDate billingMonthFor(LocalDate purchaseDate, int closingDay) {
        LocalDate firstDayOfMonth = purchaseDate.withDayOfMonth(1);
        return purchaseDate.getDayOfMonth() > closingDay ? firstDayOfMonth.plusMonths(1) : firstDayOfMonth;
    }

    private CreditCardInvoice getOrCreateInvoice(CreditCard card, LocalDate referenceMonth) {
        return invoiceRepository.findByCreditCardIdAndReferenceMonth(card.getId(), referenceMonth)
                .orElseGet(() -> {
                    LocalDate closingDate = clampToMonth(referenceMonth, card.getClosingDay());
                    LocalDate dueDate = card.getDueDay() <= card.getClosingDay()
                            ? clampToMonth(referenceMonth.plusMonths(1), card.getDueDay())
                            : clampToMonth(referenceMonth, card.getDueDay());

                    CreditCardInvoice invoice = CreditCardInvoice.builder()
                            .creditCardId(card.getId())
                            .referenceMonth(referenceMonth)
                            .closingDate(closingDate)
                            .dueDate(dueDate)
                            .status(CreditCardInvoiceStatus.OPEN)
                            .build();
                    return invoiceRepository.save(invoice);
                });
    }

    private LocalDate clampToMonth(LocalDate monthFirstDay, int day) {
        return monthFirstDay.withDayOfMonth(Math.min(day, monthFirstDay.lengthOfMonth()));
    }

    private CreditCard findOwnedCardOrThrow(Long userId, Long creditCardId) {
        return creditCardRepository.findByIdAndUserId(creditCardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cartão não encontrado: " + creditCardId));
    }

    private CreditCardPurchaseResponse toPurchaseResponse(CreditCardPurchase purchase, List<CreditCardInstallment> installments) {
        List<CreditCardInstallmentResponse> installmentResponses = installments.stream()
                .map(inst -> new CreditCardInstallmentResponse(
                        inst.getId(),
                        purchase.getId(),
                        purchase.getDescription(),
                        purchase.getStore(),
                        purchase.getCategoryId(),
                        inst.getInstallmentNumber(),
                        purchase.getInstallmentCount(),
                        inst.getAmount(),
                        inst.getDueDate(),
                        inst.getStatus()
                ))
                .toList();

        return new CreditCardPurchaseResponse(
                purchase.getId(),
                purchase.getCreditCardId(),
                purchase.getCategoryId(),
                purchase.getDescription(),
                purchase.getStore(),
                purchase.getTotalAmount(),
                purchase.getInstallmentCount(),
                purchase.getPurchaseDate(),
                purchase.getNotes(),
                installmentResponses
        );
    }
}
