package com.financas.dto.response;

import com.financas.domain.enums.PaymentMethod;
import com.financas.domain.enums.PlannedPurchasePriority;
import com.financas.domain.enums.PlannedPurchaseStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PlannedPurchaseResponse(
        Long id,
        Long categoryId,
        String itemName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal totalPrice,
        PaymentMethod expectedPaymentMethod,
        PlannedPurchasePriority priority,
        PlannedPurchaseStatus status,
        LocalDate purchaseDate,
        Long transactionId,
        Long creditCardPurchaseId,
        String notes
) {
}