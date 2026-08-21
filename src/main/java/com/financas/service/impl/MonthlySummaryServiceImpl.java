package com.financas.service.impl;

import com.financas.domain.enums.CategoryContext;
import com.financas.dto.response.MonthlySummaryResponse;
import com.financas.repository.CategoryRepository;
import com.financas.repository.FinancialTransactionRepository;
import com.financas.repository.MonthlySummaryProjection;
import com.financas.service.MonthlySummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MonthlySummaryServiceImpl implements MonthlySummaryService {

    // Convenção: a categoria de contexto INCOME chamada "Renda extra" é o que
    // separa Renda Extra de Salário na aba Geral. Se o usuário não tiver essa
    // categoria criada, extraIncome fica zerado e tudo cai como salário —
    // mesmo comportamento inofensivo do resto do sistema quando falta seed.
    private static final String EXTRA_INCOME_CATEGORY_NAME = "Renda extra";

    private final FinancialTransactionRepository financialTransactionRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public MonthlySummaryResponse getByMonth(Long userId, LocalDate referenceMonth) {
        LocalDate monthStart = referenceMonth.withDayOfMonth(1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

        MonthlySummaryProjection totals = financialTransactionRepository.findMonthlySummary(userId, monthStart, monthEnd);

        BigDecimal income = nvl(totals != null ? totals.getIncome() : null);
        BigDecimal fixedExpenses = nvl(totals != null ? totals.getFixedExpenses() : null);
        BigDecimal creditCard = nvl(totals != null ? totals.getCreditCard() : null);
        BigDecimal variableExpenses = nvl(totals != null ? totals.getVariableExpenses() : null);
        BigDecimal investments = nvl(totals != null ? totals.getInvestments() : null);

        BigDecimal extraIncome = categoryRepository
                .findByUserIdAndNameIgnoreCaseAndContext(userId, EXTRA_INCOME_CATEGORY_NAME, CategoryContext.INCOME)
                .map(category -> nvl(financialTransactionRepository
                        .sumAmountByCategoryAndPeriod(userId, category.getId(), monthStart, monthEnd)))
                .orElse(BigDecimal.ZERO);

        BigDecimal salary = income.subtract(extraIncome);

        BigDecimal result = income
                .subtract(fixedExpenses)
                .subtract(creditCard)
                .subtract(variableExpenses)
                .subtract(investments);

        return new MonthlySummaryResponse(
                monthStart, salary, extraIncome, fixedExpenses, creditCard, variableExpenses, investments, result);
    }

    @Override
    public List<MonthlySummaryResponse> getByYear(Long userId, int year) {
        List<MonthlySummaryResponse> months = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            months.add(getByMonth(userId, LocalDate.of(year, month, 1)));
        }
        return months;
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
