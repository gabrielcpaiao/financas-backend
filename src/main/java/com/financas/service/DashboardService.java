package com.financas.service;

import com.financas.dto.response.DashboardSummaryResponse;

import java.time.YearMonth;

public interface DashboardService {
    DashboardSummaryResponse summary(Long userId, YearMonth month);
}