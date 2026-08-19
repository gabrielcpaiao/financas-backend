package com.financas.dto.response;

public record AuthResponse(
        String token,
        String tokenType,
        // userId,
        String name,
        String email
) {
    public static AuthResponse of(String token, String name, String email) {
        return new AuthResponse(token, "Bearer", name, email);
    }
}
