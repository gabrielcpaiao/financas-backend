package com.financas.dto.request;

// Exatamente um dos dois deve ser informado — validado no service, não dá
// pra expressar "exatamente um de dois campos" com bean validation simples.
public record PlannedPurchaseMarkDoneRequest(
        Long transactionId,
        Long creditCardPurchaseId
) {
}