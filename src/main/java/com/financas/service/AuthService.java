package com.financas.service;

import com.financas.dto.request.LoginRequest;
import com.financas.dto.request.RegisterRequest;
import com.financas.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
