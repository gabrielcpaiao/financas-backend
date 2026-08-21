package com.financas.controller;

import com.financas.dto.request.FinancialGoalRequest;
import com.financas.dto.response.FinancialGoalResponse;
import com.financas.security.AuthenticatedUser;
import com.financas.service.FinancialGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/financial-goals")
@RequiredArgsConstructor
public class FinancialGoalController {

    private final FinancialGoalService financialGoalService;

    @GetMapping
    public List<FinancialGoalResponse> list() {
        return financialGoalService.listActive(AuthenticatedUser.currentUserId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FinancialGoalResponse create(@Valid @RequestBody FinancialGoalRequest request) {
        return financialGoalService.create(AuthenticatedUser.currentUserId(), request);
    }

    @PutMapping("/{id}")
    public FinancialGoalResponse update(@PathVariable Long id, @Valid @RequestBody FinancialGoalRequest request) {
        return financialGoalService.update(AuthenticatedUser.currentUserId(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        financialGoalService.deactivate(AuthenticatedUser.currentUserId(), id);
    }
}