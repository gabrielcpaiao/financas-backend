package com.financas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreditCardResponse(
        Long id,
        String name,
        String brand,
        BigDecimal creditLimit,
        Integer closingDay,
        Integer dueDay,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}