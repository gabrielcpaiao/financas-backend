package com.financas.dto.response;

import com.financas.domain.enums.CreditCardInvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CreditCardInvoiceResponse(
        Long id,
        Long creditCardId,
        LocalDate referenceMonth,
        LocalDate closingDate,
        LocalDate dueDate,
        CreditCardInvoiceStatus status,
        LocalDateTime paidAt,
        BigDecimal totalAmount,
        List<CreditCardInstallmentResponse> installments
) {
}
