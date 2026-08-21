package com.financas.repository;

import com.financas.domain.FinancialGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {
    List<FinancialGoal> findByUserIdAndActiveTrue(Long userId);
    Optional<FinancialGoal> findByIdAndUserId(Long id, Long userId);
}