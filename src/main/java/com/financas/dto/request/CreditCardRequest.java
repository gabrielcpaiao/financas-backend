package com.financas.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreditCardRequest(

        @NotBlank(message = "O nome do cartão é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String name,

        @Size(max = 50, message = "A bandeira deve ter no máximo 50 caracteres")
        String brand,

        @NotNull(message = "O limite é obrigatório")
        @DecimalMin(
                value = "0.01",
                message = "O limite deve ser maior que zero"
        )
        BigDecimal creditLimit,

        @NotNull(message = "O dia de fechamento é obrigatório")
        @Min(value = 1, message = "O dia de fechamento deve estar entre 1 e 31")
        @Max(value = 31, message = "O dia de fechamento deve estar entre 1 e 31")
        Integer closingDay,

        @NotNull(message = "O dia de vencimento é obrigatório")
        @Min(value = 1, message = "O dia de vencimento deve estar entre 1 e 31")
        @Max(value = 31, message = "O dia de vencimento deve estar entre 1 e 31")
        Integer dueDay
) {
}