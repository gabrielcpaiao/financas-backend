package com.financas.exception;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {

    private final String errorCode;
    private final String field;

    public ConflictException(String message) {
        super(message);
        this.errorCode = "CONFLICT";
        this.field = null;
    }

    public ConflictException(
            String message,
            String errorCode,
            String field
    ) {
        super(message);
        this.errorCode = errorCode;
        this.field = field;
    }

    public static ConflictException duplicateCategoryName(String name) {
        return new ConflictException(
                String.format(
                        "Já existe uma categoria com o nome '%s'",
                        name
                ),
                "DUPLICATE_CATEGORY_NAME",
                "name"
        );
    }

    public static ConflictException duplicateCreditCardName(String name) {
        return new ConflictException(
                String.format(
                        "Já existe um cartão de crédito com o nome '%s'",
                        name
                ),
                "DUPLICATE_CREDIT_CARD_NAME",
                "name"
        );
    }
}