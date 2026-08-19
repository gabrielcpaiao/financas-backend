package com.financas.repository;

import java.math.BigDecimal;

public interface MonthlySummaryProjection {
    BigDecimal getFixedExpenses();
    BigDecimal getCreditCard();
    BigDecimal getVariableExpenses();
    BigDecimal getIncome();
    BigDecimal getInvestments();
}