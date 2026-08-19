package com.financas.dto.request;

import com.financas.domain.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AccountRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100)
        String name,

        @NotNull(message = "O tipo é obrigatório")
        AccountType type,

        @NotNull(message = "O saldo inicial é obrigatório")
        BigDecimal initialBalance
) {
}