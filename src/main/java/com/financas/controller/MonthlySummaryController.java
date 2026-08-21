package com.financas.controller;

import com.financas.dto.response.MonthlySummaryResponse;
import com.financas.security.AuthenticatedUser;
import com.financas.service.MonthlySummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/monthly-summary")
@RequiredArgsConstructor
public class MonthlySummaryController {

    private final MonthlySummaryService monthlySummaryService;

    // GET /api/v1/monthly-summary?referenceMonth=2026-08-01
    @GetMapping
    public MonthlySummaryResponse getByMonth(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceMonth) {
        return monthlySummaryService.getByMonth(AuthenticatedUser.currentUserId(), referenceMonth);
    }

    // GET /api/v1/monthly-summary/annual?year=2026 — a tabela "Geral" completa do Excel
    @GetMapping("/annual")
    public List<MonthlySummaryResponse> getByYear(@RequestParam int year) {
        return monthlySummaryService.getByYear(AuthenticatedUser.currentUserId(), year);
    }
}
