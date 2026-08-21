package com.financas.service.impl;

import com.financas.domain.Category;
import com.financas.domain.MonthlyBudget;
import com.financas.dto.request.MonthlyBudgetRequest;
import com.financas.dto.response.MonthlyBudgetResponse;
import com.financas.exception.ResourceNotFoundException;
import com.financas.repository.CategoryRepository;
import com.financas.repository.FinancialTransactionRepository;
import com.financas.repository.MonthlyBudgetRepository;
import com.financas.service.MonthlyBudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonthlyBudgetServiceImpl implements MonthlyBudgetService {

    private final MonthlyBudgetRepository monthlyBudgetRepository;
    private final CategoryRepository categoryRepository;
    // Requer o método sumAmountByCategoryAndPeriod — ver
    // ADICIONAR_FinancialTransactionRepository.txt na raiz do backend.
    private final FinancialTransactionRepository financialTransactionRepository;

    @Override
    public MonthlyBudgetResponse upsert(Long userId, MonthlyBudgetRequest request) {
        Category category = categoryRepository.findByIdAndUserId(request.categoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + request.categoryId()));

        LocalDate referenceMonth = request.referenceMonth().withDayOfMonth(1);

        MonthlyBudget budget = monthlyBudgetRepository
                .findByUserIdAndCategoryIdAndReferenceMonth(userId, request.categoryId(), referenceMonth)
                .orElseGet(() -> MonthlyBudget.builder()
                        .userId(userId)
                        .categoryId(request.categoryId())
                        .referenceMonth(referenceMonth)
                        .build());

        budget.setPlannedAmount(request.plannedAmount());
        budget = monthlyBudgetRepository.save(budget);

        return toResponse(budget, category);
    }

    @Override
    public List<MonthlyBudgetResponse> listByMonth(Long userId, LocalDate referenceMonth) {
        LocalDate normalizedMonth = referenceMonth.withDayOfMonth(1);
        List<MonthlyBudget> budgets = monthlyBudgetRepository.findByUserIdAndReferenceMonth(userId, normalizedMonth);

        Map<Long, Category> categoriesById = categoryRepository
                .findAllById(budgets.stream().map(MonthlyBudget::getCategoryId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(Category::getId, c -> c));

        return budgets.stream()
                .map(budget -> toResponse(budget, categoriesById.get(budget.getCategoryId())))
                .toList();
    }

    private MonthlyBudgetResponse toResponse(MonthlyBudget budget, Category category) {
        LocalDate monthStart = budget.getReferenceMonth();
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

        BigDecimal realized = financialTransactionRepository.sumAmountByCategoryAndPeriod(
                budget.getUserId(), budget.getCategoryId(), monthStart, monthEnd);

        return new MonthlyBudgetResponse(
                budget.getId(),
                budget.getCategoryId(),
                category != null ? category.getName() : null,
                budget.getReferenceMonth(),
                budget.getPlannedAmount(),
                realized,
                realized.subtract(budget.getPlannedAmount())
        );
    }
}