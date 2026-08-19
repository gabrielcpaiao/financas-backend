package com.financas.service;

import com.financas.dto.request.AccountRequest;
import com.financas.dto.response.AccountResponse;

import java.util.List;

public interface AccountService {
    List<AccountResponse> listActive(Long userId);
    AccountResponse create(Long userId, AccountRequest request);
    AccountResponse update(Long userId, Long id, AccountRequest request);
    void deactivate(Long userId, Long id);
}