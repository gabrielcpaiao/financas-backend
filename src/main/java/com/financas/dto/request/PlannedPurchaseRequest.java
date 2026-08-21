package com.financas.dto.request;

import com.financas.domain.enums.PaymentMethod;
import com.financas.domain.enums.PlannedPurchasePriority;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PlannedPurchaseRequest(

        Long categoryId,

        @NotBlank(message = "O nome do item é obrigatório")
        @Size(max = 150)
        String itemName,

        @NotNull(message = "O valor unitário é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
        BigDecimal unitPrice,

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser ao menos 1")
        Integer quantity,

        PaymentMethod expectedPaymentMethod,

        @NotNull(message = "A prioridade é obrigatória")
        PlannedPurchasePriority priority,

        @Size(max = 255)
        String notes
) {
}