package com.financas.dto.request;

import com.financas.domain.enums.ExpenseType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreditCardPurchaseRequest(

        @NotNull(message = "A categoria é obrigatória")
        Long categoryId,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 150)
        String description,

        @Size(max = 100)
        String store,

        @NotNull(message = "O valor total é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
        BigDecimal totalAmount,

        @NotNull(message = "O número de parcelas é obrigatório")
        @Min(value = 1, message = "Deve haver ao menos 1 parcela")
        @Max(value = 48, message = "Número de parcelas muito alto")
        Integer installmentCount,

        @NotNull(message = "A data da compra é obrigatória")
        LocalDate purchaseDate,

        @Size(max = 255)
        String notes,

        // Opcional: se não informado, a compra é tratada como VARIABLE.
        // Ao contrário de despesas fixas/variáveis normais, uma compra no
        // cartão não tem um campo próprio no schema — o expense_type é
        // decidido aqui e só é persistido no financial_transaction gerado
        // para cada parcela.
        ExpenseType expenseType
) {
}
