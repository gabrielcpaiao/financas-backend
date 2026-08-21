package com.financas.controller;

import com.financas.dto.request.CreditCardInvoicePaymentRequest;
import com.financas.dto.response.CreditCardInvoiceResponse;
import com.financas.security.AuthenticatedUser;
import com.financas.service.CreditCardInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CreditCardInvoiceController {

    private final CreditCardInvoiceService creditCardInvoiceService;

    @GetMapping("/api/v1/credit-cards/{cardId}/invoices")
    public List<CreditCardInvoiceResponse> listByCard(@PathVariable Long cardId) {
        return creditCardInvoiceService.listByCard(AuthenticatedUser.currentUserId(), cardId);
    }

    @GetMapping("/api/v1/credit-card-invoices/{invoiceId}")
    public CreditCardInvoiceResponse getById(@PathVariable Long invoiceId) {
        return creditCardInvoiceService.getById(AuthenticatedUser.currentUserId(), invoiceId);
    }

    @PostMapping("/api/v1/credit-card-invoices/{invoiceId}/pay")
    public CreditCardInvoiceResponse pay(@PathVariable Long invoiceId,
                                         @Valid @RequestBody CreditCardInvoicePaymentRequest request) {
        return creditCardInvoiceService.pay(AuthenticatedUser.currentUserId(), invoiceId, request);
    }
}
