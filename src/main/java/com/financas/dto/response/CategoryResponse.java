package com.financas.dto.response;

import com.financas.domain.enums.CategoryContext;

public record CategoryResponse(
        Long id,
        String name,
        String color,
        CategoryContext context,
        Long parentId,
        Boolean active
) {
}
