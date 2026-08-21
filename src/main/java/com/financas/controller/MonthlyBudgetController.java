package com.financas.controller;

import com.financas.dto.request.MonthlyBudgetRequest;
import com.financas.dto.response.MonthlyBudgetResponse;
import com.financas.security.AuthenticatedUser;
import com.financas.service.MonthlyBudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/monthly-budgets")
@RequiredArgsConstructor
public class MonthlyBudgetController {

    private final MonthlyBudgetService monthlyBudgetService;

    // Ex: GET /api/v1/monthly-budgets?referenceMonth=2026-08-01
    // Cobre Renda Extra (categoria INCOME), limite de gastos variáveis e
    // qualquer outra categoria orçada — planejado x realizado num só endpoint.
    @GetMapping
    public List<MonthlyBudgetResponse> listByMonth(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceMonth) {
        return monthlyBudgetService.listByMonth(AuthenticatedUser.currentUserId(), referenceMonth);
    }

    @PostMapping
    public MonthlyBudgetResponse upsert(@Valid @RequestBody MonthlyBudgetRequest request) {
        return monthlyBudgetService.upsert(AuthenticatedUser.currentUserId(), request);
    }
}