package com.financas.service.impl;

import com.financas.domain.*;
import com.financas.domain.enums.CreditCardInstallmentStatus;
import com.financas.domain.enums.CreditCardInvoiceStatus;
import com.financas.domain.enums.PaymentMethod;
import com.financas.domain.enums.TransactionType;
import com.financas.dto.request.CreditCardInvoicePaymentRequest;
import com.financas.dto.response.CreditCardInstallmentResponse;
import com.financas.dto.response.CreditCardInvoiceResponse;
import com.financas.exception.ResourceNotFoundException;
import com.financas.repository.*;
import com.financas.service.CreditCardInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreditCardInvoiceServiceImpl implements CreditCardInvoiceService {

    private final CreditCardRepository creditCardRepository;
    private final CreditCardInvoiceRepository invoiceRepository;
    private final CreditCardInstallmentRepository installmentRepository;
    private final CreditCardPurchaseRepository purchaseRepository;
    private final AccountRepository accountRepository;
    private final FinancialTransactionRepository financialTransactionRepository;

    @Override
    public List<CreditCardInvoiceResponse> listByCard(Long userId, Long creditCardId) {
        findOwnedCardOrThrow(userId, creditCardId);
        return invoiceRepository.findByCreditCardIdOrderByReferenceMonthDesc(creditCardId).stream()
                .map(invoice -> toInvoiceResponse(invoice, installmentRepository.findByInvoiceId(invoice.getId())))
                .toList();
    }

    @Override
    public CreditCardInvoiceResponse getById(Long userId, Long invoiceId) {
        CreditCardInvoice invoice = findOwnedInvoiceOrThrow(userId, invoiceId);
        return toInvoiceResponse(invoice, installmentRepository.findByInvoiceId(invoice.getId()));
    }

    @Override
    @Transactional
    public CreditCardInvoiceResponse pay(Long userId, Long invoiceId, CreditCardInvoicePaymentRequest request) {
        CreditCardInvoice invoice = findOwnedInvoiceOrThrow(userId, invoiceId);
        CreditCard card = creditCardRepository.findById(invoice.getCreditCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Cartão não encontrado"));

        if (invoice.getStatus() == CreditCardInvoiceStatus.PAID) {
            throw new IllegalStateException("Fatura já foi paga");
        }
        if (request.paymentMethod() == PaymentMethod.CREDIT_CARD) {
            throw new IllegalArgumentException("O pagamento da fatura não pode ser feito com cartão de crédito");
        }
        accountRepository.findByIdAndUserId(request.sourceAccountId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: " + request.sourceAccountId()));

        List<CreditCardInstallment> installments = installmentRepository.findByInvoiceId(invoiceId);
        BigDecimal total = installments.stream()
                .map(CreditCardInstallment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        FinancialTransaction payment = FinancialTransaction.builder()
                .userId(userId)
                .sourceAccountId(request.sourceAccountId())
                .destinationAccountId(null)
                .categoryId(null)
                .type(TransactionType.CARD_PAYMENT)
                .transactionDate(request.paymentDate())
                .description("Pagamento fatura " + card.getName() + " - " + invoice.getReferenceMonth())
                .amount(total)
                .expenseType(null)
                .paymentMethod(request.paymentMethod())
                .notes(request.notes())
                .creditCardInvoiceId(invoice.getId())
                .build();
        financialTransactionRepository.save(payment);

        LocalDateTime now = LocalDateTime.now();
        installments.forEach(installment -> {
            installment.setStatus(CreditCardInstallmentStatus.PAID);
            installment.setPaidAt(now);
        });
        installmentRepository.saveAll(installments);

        invoice.setStatus(CreditCardInvoiceStatus.PAID);
        invoice.setPaidAt(now);
        invoice = invoiceRepository.save(invoice);

        return toInvoiceResponse(invoice, installments);
    }

    private CreditCardInvoiceResponse toInvoiceResponse(CreditCardInvoice invoice, List<CreditCardInstallment> installments) {
        Map<Long, CreditCardPurchase> purchasesById = purchaseRepository
                .findAllById(installments.stream().map(CreditCardInstallment::getCreditCardPurchaseId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(CreditCardPurchase::getId, p -> p));

        List<CreditCardInstallmentResponse> installmentResponses = installments.stream()
                .sorted(Comparator.comparing(CreditCardInstallment::getId))
                .map(installment -> {
                    CreditCardPurchase purchase = purchasesById.get(installment.getCreditCardPurchaseId());
                    return new CreditCardInstallmentResponse(
                            installment.getId(),
                            purchase.getId(),
                            purchase.getDescription(),
                            purchase.getStore(),
                            purchase.getCategoryId(),
                            installment.getInstallmentNumber(),
                            purchase.getInstallmentCount(),
                            installment.getAmount(),
                            installment.getDueDate(),
                            installment.getStatus()
                    );
                })
                .toList();

        BigDecimal total = installments.stream()
                .map(CreditCardInstallment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CreditCardInvoiceResponse(
                invoice.getId(),
                invoice.getCreditCardId(),
                invoice.getReferenceMonth(),
                invoice.getClosingDate(),
                invoice.getDueDate(),
                invoice.getStatus(),
                invoice.getPaidAt(),
                total,
                installmentResponses
        );
    }

    private CreditCard findOwnedCardOrThrow(Long userId, Long creditCardId) {
        return creditCardRepository.findByIdAndUserId(creditCardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cartão não encontrado: " + creditCardId));
    }

    private CreditCardInvoice findOwnedInvoiceOrThrow(Long userId, Long invoiceId) {
        CreditCardInvoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada: " + invoiceId));
        // Ownership indireto: a fatura pertence a um cartão, que pertence ao usuário.
        findOwnedCardOrThrow(userId, invoice.getCreditCardId());
        return invoice;
    }
}
