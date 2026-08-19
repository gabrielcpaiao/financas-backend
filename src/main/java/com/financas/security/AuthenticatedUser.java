package com.financas.security;

import org.springframework.security.core.context.SecurityContextHolder;

// Helper central: TODA query no service deve passar por currentUserId(),
// nunca confiar num user_id vindo do corpo da requisição (ver arquitetura-tecnica.md, 2.4).
public class AuthenticatedUser {

    private AuthenticatedUser() {
    }

    public static Long currentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
