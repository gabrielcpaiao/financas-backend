package com.financas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreditCardPurchaseResponse(
        Long id,
        Long creditCardId,
        Long categoryId,
        String description,
        String store,
        BigDecimal totalAmount,
        Integer installmentCount,
        LocalDate purchaseDate,
        String notes,
        List<CreditCardInstallmentResponse> installments
) {
}
