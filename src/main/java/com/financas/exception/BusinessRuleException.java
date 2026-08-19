package com.financas.exception;

import lombok.Getter;

/**
 * Exceção para violações de regras de negócio (HTTP 400).
 * Exemplos: categoria pai inválida, tentativa de criar
 * hierarquia inválida, etc.
 */
@Getter
public class BusinessRuleException extends RuntimeException {

    private final String errorCode;
    private final String field;

    public BusinessRuleException(String message) {
        super(message);
        this.errorCode = "BUSINESS_RULE_ERROR";
        this.field = null;
    }

    public BusinessRuleException(
            String message,
            String errorCode
    ) {
        super(message);
        this.errorCode = errorCode;
        this.field = null;
    }

    public BusinessRuleException(
            String message,
            String errorCode,
            String field
    ) {
        super(message);
        this.errorCode = errorCode;
        this.field = field;
    }

    public static BusinessRuleException categoryCannotBeItsOwnParent() {
        return new BusinessRuleException(
                "Uma categoria não pode ser pai dela mesma",
                "CATEGORY_CANNOT_BE_ITS_OWN_PARENT",
                "parentId"
        );
    }

    public static BusinessRuleException parentCategoryInactive() {
        return new BusinessRuleException(
                "A categoria pai está desativada",
                "PARENT_CATEGORY_INACTIVE",
                "parentId"
        );
    }

    public static BusinessRuleException parentContextMismatch() {
        return new BusinessRuleException(
                "A categoria pai deve possuir o mesmo contexto da categoria",
                "PARENT_CONTEXT_MISMATCH",
                "context"
        );
    }

    public static BusinessRuleException categoryHasActiveChildren() {
        return new BusinessRuleException(
                "Não é possível desativar uma categoria que possui categorias filhas ativas",
                "CATEGORY_HAS_ACTIVE_CHILDREN",
                "id"
        );
    }
}