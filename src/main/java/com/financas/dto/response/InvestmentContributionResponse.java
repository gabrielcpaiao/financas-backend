package com.financas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestmentContributionResponse(
        Long id,
        Long investmentId,
        Long accountId,
        LocalDate contributionDate,
        BigDecimal amount,
        String notes
) {
}