package com.financas.dto.response;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
        String referenceMonth,
        BigDecimal income,
        BigDecimal fixedExpenses,
        BigDecimal creditCard,
        BigDecimal variableExpenses,
        BigDecimal investments,
        BigDecimal result
) {
}