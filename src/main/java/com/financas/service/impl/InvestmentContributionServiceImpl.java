package com.financas.service.impl;

import com.financas.domain.FinancialTransaction;
import com.financas.domain.Investment;
import com.financas.domain.InvestmentContribution;
import com.financas.domain.enums.TransactionType;
import com.financas.dto.request.InvestmentContributionRequest;
import com.financas.dto.response.InvestmentContributionResponse;
import com.financas.exception.ResourceNotFoundException;
import com.financas.repository.*;
import com.financas.service.InvestmentContributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestmentContributionServiceImpl implements InvestmentContributionService {

    private final InvestmentRepository investmentRepository;
    private final InvestmentContributionRepository investmentContributionRepository;
    private final AccountRepository accountRepository;
    private final FinancialTransactionRepository financialTransactionRepository;

    @Override
    public List<InvestmentContributionResponse> listByInvestment(Long userId, Long investmentId) {
        findOwnedInvestmentOrThrow(userId, investmentId);
        return investmentContributionRepository
                .findByInvestmentIdOrderByContributionDateDescIdDesc(investmentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public InvestmentContributionResponse create(Long userId, Long investmentId, InvestmentContributionRequest request) {
        Investment investment = findOwnedInvestmentOrThrow(userId, investmentId);
        accountRepository.findByIdAndUserId(request.accountId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: " + request.accountId()));

        // O aporte debita a conta de origem na hora (diferente do cartão de
        // crédito): dinheiro sai da conta e vira patrimônio investido.
        FinancialTransaction transaction = FinancialTransaction.builder()
                .userId(userId)
                .sourceAccountId(request.accountId())
                .destinationAccountId(null)
                .categoryId(null)
                .type(TransactionType.INVESTMENT)
                .transactionDate(request.contributionDate())
                .description("Aporte - " + investment.getName())
                .amount(request.amount())
                .expenseType(null)
                .paymentMethod(null)
                .notes(request.notes())
                .build();
        transaction = financialTransactionRepository.save(transaction);

        InvestmentContribution contribution = InvestmentContribution.builder()
                .investmentId(investmentId)
                .accountId(request.accountId())
                .transactionId(transaction.getId())
                .contributionDate(request.contributionDate())
                .amount(request.amount())
                .notes(request.notes())
                .build();
        contribution = investmentContributionRepository.save(contribution);

        return toResponse(contribution);
    }

    private Investment findOwnedInvestmentOrThrow(Long userId, Long investmentId) {
        return investmentRepository.findByIdAndUserId(investmentId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Investimento não encontrado: " + investmentId));
    }

    private InvestmentContributionResponse toResponse(InvestmentContribution contribution) {
        return new InvestmentContributionResponse(
                contribution.getId(),
                contribution.getInvestmentId(),
                contribution.getAccountId(),
                contribution.getContributionDate(),
                contribution.getAmount(),
                contribution.getNotes()
        );
    }
}