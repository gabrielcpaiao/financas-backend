package com.financas.repository;

import com.financas.domain.InvestmentContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface InvestmentContributionRepository extends JpaRepository<InvestmentContribution, Long> {

    List<InvestmentContribution> findByInvestmentIdOrderByContributionDateDescIdDesc(Long investmentId);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM InvestmentContribution c WHERE c.investmentId = :investmentId")
    BigDecimal sumAmountByInvestmentId(@Param("investmentId") Long investmentId);
}