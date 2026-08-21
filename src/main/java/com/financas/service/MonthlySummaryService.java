package com.financas.service;

import com.financas.dto.response.MonthlySummaryResponse;

import java.time.LocalDate;
import java.util.List;

public interface MonthlySummaryService {
    MonthlySummaryResponse getByMonth(Long userId, LocalDate referenceMonth);
    // Cobre a linha "Total anual" da aba Geral do Excel — 12 meses + soma.
    List<MonthlySummaryResponse> getByYear(Long userId, int year);
}
