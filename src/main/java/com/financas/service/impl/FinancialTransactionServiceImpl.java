package com.financas.service.impl;

import com.financas.domain.FinancialTransaction;
import com.financas.domain.enums.PaymentMethod;
import com.financas.domain.enums.TransactionType;
import com.financas.dto.request.FinancialTransactionRequest;
import com.financas.dto.response.FinancialTransactionResponse;
import com.financas.exception.BusinessRuleException;
import com.financas.exception.ResourceNotFoundException;
import com.financas.mapper.FinancialTransactionMapper;
import com.financas.repository.AccountRepository;
import com.financas.repository.CategoryRepository;
import com.financas.repository.FinancialTransactionRepository;
import com.financas.service.FinancialTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialTransactionServiceImpl implements FinancialTransactionService {

    private final FinancialTransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final FinancialTransactionMapper transactionMapper;

    @Override
    public List<FinancialTransactionResponse> listByPeriod(Long userId, LocalDate from, LocalDate to) {
        return transactionRepository
                .findByUserIdAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(userId, from, to).stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    @Override
    public FinancialTransactionResponse create(Long userId, FinancialTransactionRequest request) {
        validateBusinessRules(userId, request);
        FinancialTransaction transaction = transactionMapper.toEntity(request);
        transaction.setUserId(userId);
        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    public FinancialTransactionResponse update(Long userId, Long id, FinancialTransactionRequest request) {
        FinancialTransaction transaction = findOwnedOrThrow(userId, id);
        validateBusinessRules(userId, request);

        transaction.setType(request.type());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setDescription(request.description());
        transaction.setAmount(request.amount());
        transaction.setSourceAccountId(request.sourceAccountId());
        transaction.setDestinationAccountId(request.destinationAccountId());
        transaction.setCategoryId(request.categoryId());
        transaction.setExpenseType(request.expenseType());
        transaction.setPaymentMethod(request.paymentMethod());
        transaction.setNotes(request.notes());

        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    public void delete(Long userId, Long id) {
        // Diferente de Account/Category: financial_transaction não tem coluna
        // `active` no schema — aqui o delete é de verdade (hard delete).
        // FKs que apontam pra cá (planned_purchase, investment_contribution)
        // são ON DELETE SET NULL, então apagar é seguro no banco.
        transactionRepository.delete(findOwnedOrThrow(userId, id));
    }

    private FinancialTransaction findOwnedOrThrow(Long userId, Long id) {
        return transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Lançamento não encontrado: " + id));
    }

    // ---------------------------------------------------------------
    // Espelha as CHECK constraints de financial_transaction (V1 + V2)
    // em Java, pra devolver 400 com mensagem clara em vez do MySQL
    // rejeitar com um erro de CHECK constraint ilegível pro usuário.
    // O CHECK do banco continua existindo como rede de segurança.
    // ---------------------------------------------------------------
    private void validateBusinessRules(Long userId, FinancialTransactionRequest request) {
        TransactionType type = request.type();

        if (type == TransactionType.CARD_PAYMENT) {
            throw new BusinessRuleException(
                    "Pagamento de fatura ainda não está disponível — depende do módulo de Cartão (Fase 4)");
        }
        if (type == TransactionType.INVESTMENT) {
            throw new BusinessRuleException(
                    "Aporte de investimento ainda não está disponível — depende do módulo de Investimentos (Fase 5)");
        }

        switch (type) {
            case INCOME -> {
                requireNull(request.sourceAccountId(), "sourceAccountId deve ser nulo para INCOME");
                requireNotNull(request.destinationAccountId(), "destinationAccountId é obrigatório para INCOME");
                requireNotNull(request.categoryId(), "categoryId é obrigatório para INCOME");
                requireNull(request.expenseType(), "expenseType deve ser nulo para INCOME");
                requireNotNull(request.paymentMethod(), "paymentMethod é obrigatório para INCOME");
            }
            case EXPENSE -> {
                requireNotNull(request.categoryId(), "categoryId é obrigatório para EXPENSE");
                requireNotNull(request.expenseType(), "expenseType é obrigatório para EXPENSE");
                requireNotNull(request.paymentMethod(), "paymentMethod é obrigatório para EXPENSE");

                if (request.paymentMethod() == PaymentMethod.CREDIT_CARD) {
                    requireNull(request.sourceAccountId(),
                            "sourceAccountId deve ser nulo em despesa no cartão (só é debitado no pagamento da fatura)");
                    requireNull(request.destinationAccountId(), "destinationAccountId deve ser nulo para EXPENSE");
                } else {
                    requireNotNull(request.sourceAccountId(), "sourceAccountId é obrigatório para EXPENSE fora do cartão");
                    requireNull(request.destinationAccountId(), "destinationAccountId deve ser nulo para EXPENSE");
                }
            }
            case TRANSFER -> {
                requireNotNull(request.sourceAccountId(), "sourceAccountId é obrigatório para TRANSFER");
                requireNotNull(request.destinationAccountId(), "destinationAccountId é obrigatório para TRANSFER");
                if (request.sourceAccountId() != null && request.sourceAccountId().equals(request.destinationAccountId())) {
                    throw new BusinessRuleException("Conta de origem e destino não podem ser a mesma em uma transferência");
                }
                requireNull(request.categoryId(), "categoryId deve ser nulo para TRANSFER");
                requireNull(request.expenseType(), "expenseType deve ser nulo para TRANSFER");
                requireNull(request.paymentMethod(), "paymentMethod deve ser nulo para TRANSFER");
            }
            default -> throw new BusinessRuleException("Tipo de lançamento inválido: " + type);
        }

        // Ownership: toda referência precisa pertencer ao usuário autenticado —
        // nunca confiar que um account_id/category_id do body é do dono do token.
        if (request.sourceAccountId() != null) {
            accountRepository.findByIdAndUserId(request.sourceAccountId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Conta de origem não encontrada: " + request.sourceAccountId()));
        }
        if (request.destinationAccountId() != null) {
            accountRepository.findByIdAndUserId(request.destinationAccountId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Conta de destino não encontrada: " + request.destinationAccountId()));
        }
        if (request.categoryId() != null) {
            categoryRepository.findByIdAndUserId(request.categoryId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + request.categoryId()));
        }
    }

    private void requireNull(Object value, String message) {
        if (value != null) throw new BusinessRuleException(message);
    }

    private void requireNotNull(Object value, String message) {
        if (value == null) throw new BusinessRuleException(message);
    }
}