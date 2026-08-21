package com.financas.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestmentContributionRequest(

        @NotNull(message = "A conta de origem é obrigatória")
        Long accountId,

        @NotNull(message = "A data do aporte é obrigatória")
        LocalDate contributionDate,

        @NotNull(message = "O valor é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
        BigDecimal amount,

        @Size(max = 255)
        String notes
) {
}