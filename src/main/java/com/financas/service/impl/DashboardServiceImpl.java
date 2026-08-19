package com.financas.service.impl;

import com.financas.dto.response.DashboardSummaryResponse;
import com.financas.repository.FinancialTransactionRepository;
import com.financas.repository.MonthlySummaryProjection;
import com.financas.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final FinancialTransactionRepository transactionRepository;

    @Override
    public DashboardSummaryResponse summary(Long userId, YearMonth month) {
        MonthlySummaryProjection projection = transactionRepository.findMonthlySummary(
                userId, month.atDay(1), month.atEndOfMonth());

        var income = projection.getIncome();
        var fixedExpenses = projection.getFixedExpenses();
        var creditCard = projection.getCreditCard();
        var variableExpenses = projection.getVariableExpenses();
        var investments = projection.getInvestments();

        // Mesma lógica da aba "Geral" do Excel: resultado = receitas -
        // (despesas fixas + cartão + variáveis + investimentos).
        var result = income
                .subtract(fixedExpenses)
                .subtract(creditCard)
                .subtract(variableExpenses)
                .subtract(investments);

        return new DashboardSummaryResponse(
                month.toString(), income, fixedExpenses, creditCard, variableExpenses, investments, result);
    }
}