package com.financas.dto.request;

import com.financas.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreditCardInvoicePaymentRequest(

        @NotNull(message = "A conta de origem é obrigatória")
        Long sourceAccountId,

        // Como a fatura foi quitada (débito, pix etc.) — nunca CREDIT_CARD,
        // isso é validado no service.
        @NotNull(message = "A forma de pagamento é obrigatória")
        PaymentMethod paymentMethod,

        @NotNull(message = "A data do pagamento é obrigatória")
        LocalDate paymentDate,

        @Size(max = 255)
        String notes
) {
}
