package com.financas.controller;

import com.financas.dto.request.InvestmentRequest;
import com.financas.dto.response.InvestmentResponse;
import com.financas.security.AuthenticatedUser;
import com.financas.service.InvestmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/investments")
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService investmentService;

    @GetMapping
    public List<InvestmentResponse> list() {
        return investmentService.listActive(AuthenticatedUser.currentUserId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvestmentResponse create(@Valid @RequestBody InvestmentRequest request) {
        return investmentService.create(AuthenticatedUser.currentUserId(), request);
    }

    @PutMapping("/{id}")
    public InvestmentResponse update(@PathVariable Long id, @Valid @RequestBody InvestmentRequest request) {
        return investmentService.update(AuthenticatedUser.currentUserId(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        investmentService.deactivate(AuthenticatedUser.currentUserId(), id);
    }
}