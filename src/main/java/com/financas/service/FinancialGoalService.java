package com.financas.service;

import com.financas.dto.request.FinancialGoalRequest;
import com.financas.dto.response.FinancialGoalResponse;

import java.util.List;

public interface FinancialGoalService {
    List<FinancialGoalResponse> listActive(Long userId);
    FinancialGoalResponse create(Long userId, FinancialGoalRequest request);
    FinancialGoalResponse update(Long userId, Long id, FinancialGoalRequest request);
    void deactivate(Long userId, Long id);
}