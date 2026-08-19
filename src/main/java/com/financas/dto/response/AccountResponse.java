package com.financas.dto.response;

import com.financas.domain.enums.AccountType;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String name,
        AccountType type,
        BigDecimal initialBalance,
        Boolean active
) {
}