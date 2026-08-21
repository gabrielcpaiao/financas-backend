package com.financas.dto.request;

import com.financas.domain.enums.InvestmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InvestmentRequest(

        @NotBlank(message = "O nome do investimento é obrigatório")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String name,

        @NotNull(message = "O tipo do investimento é obrigatório")
        InvestmentType type,

        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String description,

        Long financialGoalId
) {
}