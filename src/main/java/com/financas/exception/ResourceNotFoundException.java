package com.financas.exception;

/**
 * Exceção para recursos não encontrados (HTTP 404).
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}