package com.financas.controller;

import com.financas.dto.request.CreditCardRequest;
import com.financas.dto.response.CreditCardResponse;
import com.financas.security.AuthenticatedUser;
import com.financas.service.CreditCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/credit-cards")
@RequiredArgsConstructor
public class CreditCardController {

    private final CreditCardService creditCardService;

    @GetMapping
    public List<CreditCardResponse> list() {
        return creditCardService.listActive(AuthenticatedUser.currentUserId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardResponse create(@Valid @RequestBody CreditCardRequest request) {
        return creditCardService.create(AuthenticatedUser.currentUserId(), request);
    }

    @PutMapping("/{id}")
    public CreditCardResponse update(@PathVariable Long id, @Valid @RequestBody CreditCardRequest request) {
        return creditCardService.update(AuthenticatedUser.currentUserId(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        creditCardService.deactivate(AuthenticatedUser.currentUserId(), id);
    }
}
