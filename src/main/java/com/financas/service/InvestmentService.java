package com.financas.service;

import com.financas.dto.request.InvestmentRequest;
import com.financas.dto.response.InvestmentResponse;

import java.util.List;

public interface InvestmentService {
    List<InvestmentResponse> listActive(Long userId);
    InvestmentResponse create(Long userId, InvestmentRequest request);
    InvestmentResponse update(Long userId, Long id, InvestmentRequest request);
    void deactivate(Long userId, Long id);
}