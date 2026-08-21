package com.financas.repository;

import com.financas.domain.MonthlyBudget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudget, Long> {
    List<MonthlyBudget> findByUserIdAndReferenceMonth(Long userId, LocalDate referenceMonth);
    Optional<MonthlyBudget> findByUserIdAndCategoryIdAndReferenceMonth(Long userId, Long categoryId, LocalDate referenceMonth);
}