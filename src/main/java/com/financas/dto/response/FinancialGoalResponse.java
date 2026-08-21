package com.financas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinancialGoalResponse(
        Long id,
        String name,
        BigDecimal targetAmount,
        LocalDate targetDate,
        Boolean active,
        // Soma de todos os aportes de todos os investimentos ligados a essa meta.
        BigDecimal totalContributed
) {
}