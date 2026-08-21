package com.financas.controller;

import com.financas.dto.request.CreditCardPurchaseRequest;
import com.financas.dto.response.CreditCardPurchaseResponse;
import com.financas.security.AuthenticatedUser;
import com.financas.service.CreditCardPurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/credit-cards/{cardId}/purchases")
@RequiredArgsConstructor
public class CreditCardPurchaseController {

    private final CreditCardPurchaseService creditCardPurchaseService;

    @GetMapping
    public List<CreditCardPurchaseResponse> list(@PathVariable Long cardId) {
        return creditCardPurchaseService.listByCard(AuthenticatedUser.currentUserId(), cardId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardPurchaseResponse create(@PathVariable Long cardId,
                                             @Valid @RequestBody CreditCardPurchaseRequest request) {
        return creditCardPurchaseService.create(AuthenticatedUser.currentUserId(), cardId, request);
    }
}
