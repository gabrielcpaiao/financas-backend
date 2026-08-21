package com.financas.service;

import com.financas.dto.request.CreditCardPurchaseRequest;
import com.financas.dto.response.CreditCardPurchaseResponse;

import java.util.List;

public interface CreditCardPurchaseService {
    List<CreditCardPurchaseResponse> listByCard(Long userId, Long creditCardId);
    CreditCardPurchaseResponse create(Long userId, Long creditCardId, CreditCardPurchaseRequest request);
}
