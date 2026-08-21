package com.financas.service;

import com.financas.dto.request.MonthlyBudgetRequest;
import com.financas.dto.response.MonthlyBudgetResponse;

import java.time.LocalDate;
import java.util.List;

public interface MonthlyBudgetService {
    // Cria ou atualiza o planejado de uma categoria/mês (upsert por
    // uk_monthly_budget_user_category_month).
    MonthlyBudgetResponse upsert(Long userId, MonthlyBudgetRequest request);

    // Lista todos os orçamentos do mês com planejado x realizado calculado
    // a partir do financial_transaction — serve tanto pra Renda Extra (categoria
    // INCOME) quanto pra qualquer outra categoria orçada.
    List<MonthlyBudgetResponse> listByMonth(Long userId, LocalDate referenceMonth);
}