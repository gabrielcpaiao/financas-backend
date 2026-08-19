package com.financas.controller;

import com.financas.dto.response.DashboardSummaryResponse;
import com.financas.exception.BusinessRuleException;
import com.financas.security.AuthenticatedUser;
import com.financas.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryResponse summary(@RequestParam(required = false) String month) {
        return dashboardService.summary(AuthenticatedUser.currentUserId(), parseOrCurrent(month));
    }

    private YearMonth parseOrCurrent(String month) {
        if (month == null || month.isBlank()) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(month);
        } catch (DateTimeParseException e) {
            throw new BusinessRuleException("Parâmetro 'month' inválido, use o formato YYYY-MM");
        }
    }
}