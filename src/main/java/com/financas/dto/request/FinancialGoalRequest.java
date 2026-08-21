package com.financas.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinancialGoalRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 80)
        String name,

        @DecimalMin(value = "0.00", message = "O valor alvo não pode ser negativo")
        BigDecimal targetAmount,

        LocalDate targetDate
) {
}