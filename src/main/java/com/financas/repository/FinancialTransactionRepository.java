package com.financas.repository;

import com.financas.domain.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {

    List<FinancialTransaction> findByUserIdAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(
            Long userId, LocalDate from, LocalDate to);

    Optional<FinancialTransaction> findByIdAndUserId(Long id, Long userId);

    @Query(value = """
            SELECT
                COALESCE(SUM(CASE WHEN type = 'EXPENSE' AND expense_type = 'FIXED'
                              AND payment_method <> 'CREDIT_CARD' THEN amount ELSE 0.00 END), 0.00) AS fixedExpenses,
                COALESCE(SUM(CASE WHEN type = 'EXPENSE' AND payment_method = 'CREDIT_CARD'
                              THEN amount ELSE 0.00 END), 0.00) AS creditCard,
                COALESCE(SUM(CASE WHEN type = 'EXPENSE' AND expense_type = 'VARIABLE'
                              AND payment_method <> 'CREDIT_CARD' THEN amount ELSE 0.00 END), 0.00) AS variableExpenses,
                COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0.00 END), 0.00) AS income,
                COALESCE(SUM(CASE WHEN type = 'INVESTMENT' THEN amount ELSE 0.00 END), 0.00) AS investments
            FROM financial_transaction
            WHERE user_id = :userId
              AND transaction_date >= :monthStart
              AND transaction_date <= :monthEnd
            """, nativeQuery = true)
    MonthlySummaryProjection findMonthlySummary(@Param("userId") Long userId,
                                                @Param("monthStart") LocalDate monthStart,
                                                @Param("monthEnd") LocalDate monthEnd);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM FinancialTransaction t
            WHERE t.userId = :userId
              AND t.categoryId = :categoryId
              AND t.transactionDate >= :from
              AND t.transactionDate <= :to
            """)
    BigDecimal sumAmountByCategoryAndPeriod(@Param("userId") Long userId,
                                            @Param("categoryId") Long categoryId,
                                            @Param("from") LocalDate from,
                                            @Param("to") LocalDate to);
}