package com.financas.dto.request;

import com.financas.domain.enums.ExpenseType;
import com.financas.domain.enums.PaymentMethod;
import com.financas.domain.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinancialTransactionRequest(

        @NotNull(message = "O tipo é obrigatório")
        TransactionType type,

        @NotNull(message = "A data é obrigatória")
        LocalDate transactionDate,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 150)
        String description,

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser maior que zero")
        BigDecimal amount,

        Long sourceAccountId,
        Long destinationAccountId,
        Long categoryId,
        ExpenseType expenseType,
        PaymentMethod paymentMethod,

        @Size(max = 255)
        String notes
) {
}