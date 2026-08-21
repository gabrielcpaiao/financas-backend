package com.financas.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MonthlyBudgetRequest(

        @NotNull(message = "A categoria é obrigatória")
        Long categoryId,

        // Sempre normalizado para o primeiro dia do mês no service.
        @NotNull(message = "O mês de referência é obrigatório")
        LocalDate referenceMonth,

        @NotNull(message = "O valor planejado é obrigatório")
        @DecimalMin(value = "0.00", message = "O valor planejado não pode ser negativo")
        BigDecimal plannedAmount
) {
}