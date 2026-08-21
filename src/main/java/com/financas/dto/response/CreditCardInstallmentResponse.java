package com.financas.dto.response;

import com.financas.domain.enums.CreditCardInstallmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreditCardInstallmentResponse(
        Long id,
        Long purchaseId,
        String description,
        String store,
        Long categoryId,
        Integer installmentNumber,
        Integer installmentCount,
        BigDecimal amount,
        LocalDate dueDate,
        CreditCardInstallmentStatus status
) {
}
