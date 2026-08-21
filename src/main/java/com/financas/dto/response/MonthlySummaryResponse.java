package com.financas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MonthlySummaryResponse(
        LocalDate referenceMonth,
        BigDecimal salary,
        BigDecimal extraIncome,
        BigDecimal fixedExpenses,
        BigDecimal creditCardExpenses,
        BigDecimal variableExpenses,
        BigDecimal investments,
        // salary + extraIncome - fixedExpenses - creditCardExpenses - variableExpenses - investments
        BigDecimal result
) {
}
