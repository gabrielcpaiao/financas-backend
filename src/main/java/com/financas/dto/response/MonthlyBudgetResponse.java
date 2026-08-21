package com.financas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MonthlyBudgetResponse(
        Long id,
        Long categoryId,
        String categoryName,
        LocalDate referenceMonth,
        BigDecimal plannedAmount,
        BigDecimal realizedAmount,
        // realizedAmount - plannedAmount (positivo = passou do planejado, igual ao Excel)
        BigDecimal difference
) {
}