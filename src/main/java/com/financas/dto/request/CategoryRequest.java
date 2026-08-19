package com.financas.dto.request;

import com.financas.domain.enums.CategoryContext;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 80)
        String name,

        @NotBlank(message = "A cor é obrigatória")
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "A cor deve estar no formato hexadecimal, ex: #0F766E")
        String color,

        @NotNull(message = "O contexto é obrigatório (INCOME ou EXPENSE)")
        CategoryContext context,

        Long parentId
) {
}
