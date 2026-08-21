package com.financas.service;

import com.financas.dto.request.CreditCardRequest;
import com.financas.dto.response.CreditCardResponse;

import java.util.List;

public interface CreditCardService {
    List<CreditCardResponse> listActive(Long userId);
    CreditCardResponse create(Long userId, CreditCardRequest request);
    CreditCardResponse update(Long userId, Long id, CreditCardRequest request);
    void deactivate(Long userId, Long id);
}
