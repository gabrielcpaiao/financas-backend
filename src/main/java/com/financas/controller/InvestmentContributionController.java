package com.financas.controller;

import com.financas.dto.request.InvestmentContributionRequest;
import com.financas.dto.response.InvestmentContributionResponse;
import com.financas.security.AuthenticatedUser;
import com.financas.service.InvestmentContributionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/investments/{investmentId}/contributions")
@RequiredArgsConstructor
public class InvestmentContributionController {

    private final InvestmentContributionService investmentContributionService;

    @GetMapping
    public List<InvestmentContributionResponse> list(@PathVariable Long investmentId) {
        return investmentContributionService.listByInvestment(AuthenticatedUser.currentUserId(), investmentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvestmentContributionResponse create(@PathVariable Long investmentId,
                                                 @Valid @RequestBody InvestmentContributionRequest request) {
        return investmentContributionService.create(AuthenticatedUser.currentUserId(), investmentId, request);
    }
}