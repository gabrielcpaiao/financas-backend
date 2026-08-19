package com.financas.service.impl;

import com.financas.domain.AppUser;
import com.financas.dto.request.LoginRequest;
import com.financas.dto.request.RegisterRequest;
import com.financas.dto.response.AuthResponse;
import com.financas.exception.BusinessRuleException;
import com.financas.exception.InvalidCredentialsException;
import com.financas.repository.AppUserRepository;
import com.financas.security.JwtService;
import com.financas.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (appUserRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Já existe uma conta com este email");
        }

        AppUser user = AppUser.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();

        appUserRepository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return AuthResponse.of(token, user.getName(), user.getEmail());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        AppUser user = appUserRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return AuthResponse.of(token, user.getName(), user.getEmail());
    }
}