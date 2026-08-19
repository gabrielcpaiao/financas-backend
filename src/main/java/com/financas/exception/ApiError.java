package com.financas.exception;

import java.time.Instant;

// Formato único de erro, igual ao definido em arquitetura-tecnica.md.
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(Instant.now(), status, error, message, path);
    }
}
