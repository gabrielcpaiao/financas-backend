package com.financas.dto.response;

import com.financas.domain.enums.InvestmentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvestmentResponse(

        Long id,
        String name,
        InvestmentType type,
        String description,
        Long financialGoalId,
        boolean active,
        BigDecimal totalContributed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}