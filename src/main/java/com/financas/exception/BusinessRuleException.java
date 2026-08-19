package com.financas.exception;

// Use para violações de regra de negócio (ex: email já cadastrado,
// categoria em uso não pode ser removida etc.) — sempre vira HTTP 400.
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
