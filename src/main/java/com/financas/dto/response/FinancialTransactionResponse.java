package com.financas.dto.response;

import com.financas.domain.enums.ExpenseType;
import com.financas.domain.enums.PaymentMethod;
import com.financas.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinancialTransactionResponse(
        Long id,
        TransactionType type,
        LocalDate transactionDate,
        String description,
        BigDecimal amount,
        Long sourceAccountId,
        Long destinationAccountId,
        Long categoryId,
        ExpenseType expenseType,
        PaymentMethod paymentMethod,
        String notes
) {
}