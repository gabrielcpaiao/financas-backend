package com.financas.service.impl;

import com.financas.domain.Account;
import com.financas.dto.request.AccountRequest;
import com.financas.dto.response.AccountResponse;
import com.financas.exception.ResourceNotFoundException;
import com.financas.mapper.AccountMapper;
import com.financas.repository.AccountRepository;
import com.financas.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public List<AccountResponse> listActive(Long userId) {
        return accountRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    @Override
    public AccountResponse create(Long userId, AccountRequest request) {
        Account account = accountMapper.toEntity(request);
        account.setUserId(userId);
        account.setActive(true);
        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    public AccountResponse update(Long userId, Long id, AccountRequest request) {
        Account account = findOwnedOrThrow(userId, id);
        account.setName(request.name());
        account.setType(request.type());
        account.setInitialBalance(request.initialBalance());
        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    public void deactivate(Long userId, Long id) {
        // Nunca DELETE de verdade: conta pode estar referenciada por
        // financial_transaction (ON DELETE RESTRICT no schema).
        Account account = findOwnedOrThrow(userId, id);
        account.setActive(false);
        accountRepository.save(account);
    }

    private Account findOwnedOrThrow(Long userId, Long id) {
        return accountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: " + id));
    }
}