package com.financas.service;

import com.financas.dto.request.CreditCardInvoicePaymentRequest;
import com.financas.dto.response.CreditCardInvoiceResponse;

import java.util.List;

public interface CreditCardInvoiceService {
    List<CreditCardInvoiceResponse> listByCard(Long userId, Long creditCardId);
    CreditCardInvoiceResponse getById(Long userId, Long invoiceId);
    CreditCardInvoiceResponse pay(Long userId, Long invoiceId, CreditCardInvoicePaymentRequest request);
}
