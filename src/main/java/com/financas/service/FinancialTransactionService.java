package com.financas.service;

import com.financas.dto.request.FinancialTransactionRequest;
import com.financas.dto.response.FinancialTransactionResponse;

import java.time.LocalDate;
import java.util.List;

public interface FinancialTransactionService {
    List<FinancialTransactionResponse> listByPeriod(Long userId, LocalDate from, LocalDate to);
    FinancialTransactionResponse create(Long userId, FinancialTransactionRequest request);
    FinancialTransactionResponse update(Long userId, Long id, FinancialTransactionRequest request);
    void delete(Long userId, Long id);
}