package com.financas.service;

import com.financas.dto.request.InvestmentContributionRequest;
import com.financas.dto.response.InvestmentContributionResponse;

import java.util.List;

public interface InvestmentContributionService {
    List<InvestmentContributionResponse> listByInvestment(Long userId, Long investmentId);
    InvestmentContributionResponse create(Long userId, Long investmentId, InvestmentContributionRequest request);
}