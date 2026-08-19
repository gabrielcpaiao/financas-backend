package com.financas.controller;

import com.financas.dto.request.FinancialTransactionRequest;
import com.financas.dto.response.FinancialTransactionResponse;
import com.financas.security.AuthenticatedUser;
import com.financas.service.FinancialTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class FinancialTransactionController {

    private final FinancialTransactionService transactionService;

    @GetMapping
    public List<FinancialTransactionResponse> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        LocalDate effectiveFrom = from;
        LocalDate effectiveTo = to;

        // Sem período informado, assume o mês corrente — evita trazer a
        // tabela inteira por padrão (mesma organização mensal do Excel).
        if (effectiveFrom == null || effectiveTo == null) {
            YearMonth currentMonth = YearMonth.now();
            effectiveFrom = currentMonth.atDay(1);
            effectiveTo = currentMonth.atEndOfMonth();
        }

        return transactionService.listByPeriod(AuthenticatedUser.currentUserId(), effectiveFrom, effectiveTo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FinancialTransactionResponse create(@Valid @RequestBody FinancialTransactionRequest request) {
        return transactionService.create(AuthenticatedUser.currentUserId(), request);
    }

    @PutMapping("/{id}")
    public FinancialTransactionResponse update(@PathVariable Long id, @Valid @RequestBody FinancialTransactionRequest request) {
        return transactionService.update(AuthenticatedUser.currentUserId(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        transactionService.delete(AuthenticatedUser.currentUserId(), id);
    }
}