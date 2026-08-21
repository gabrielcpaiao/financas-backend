package com.financas.service.impl;

import com.financas.domain.Investment;
import com.financas.dto.request.InvestmentRequest;
import com.financas.dto.response.InvestmentResponse;
import com.financas.exception.ResourceNotFoundException;
import com.financas.mapper.InvestmentMapper;
import com.financas.repository.FinancialGoalRepository;
import com.financas.repository.InvestmentContributionRepository;
import com.financas.repository.InvestmentRepository;
import com.financas.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final FinancialGoalRepository financialGoalRepository;
    private final InvestmentContributionRepository investmentContributionRepository;
    private final InvestmentMapper investmentMapper;

    @Override
    public List<InvestmentResponse> listActive(Long userId) {
        return investmentRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public InvestmentResponse create(Long userId, InvestmentRequest request) {
        validateGoalOwnership(userId, request.financialGoalId());

        Investment investment = investmentMapper.toEntity(request);
        investment.setUserId(userId);
        investment.setFinancialGoalId(request.financialGoalId());
        investment.setActive(true);
        investment = investmentRepository.save(investment);
        return toResponse(investment);
    }

    @Override
    public InvestmentResponse update(Long userId, Long id, InvestmentRequest request) {
        validateGoalOwnership(userId, request.financialGoalId());

        Investment investment = findOwnedOrThrow(userId, id);
        investment.setName(request.name());
        investment.setType(request.type());
        investment.setDescription(request.description());
        investment.setFinancialGoalId(request.financialGoalId());
        investment = investmentRepository.save(investment);
        return toResponse(investment);
    }

    @Override
    public void deactivate(Long userId, Long id) {
        // Nunca DELETE de verdade: investimento é referenciado por
        // investment_contribution (ON DELETE CASCADE) — desativar preserva o histórico.
        Investment investment = findOwnedOrThrow(userId, id);
        investment.setActive(false);
        investmentRepository.save(investment);
    }

    private void validateGoalOwnership(Long userId, Long financialGoalId) {
        if (financialGoalId == null) return;
        financialGoalRepository.findByIdAndUserId(financialGoalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Objetivo não encontrado: " + financialGoalId));
    }

    private Investment findOwnedOrThrow(Long userId, Long id) {
        return investmentRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Investimento não encontrado: " + id));
    }

    private InvestmentResponse toResponse(Investment investment) {
        BigDecimal totalContributed = investmentContributionRepository.sumAmountByInvestmentId(investment.getId());
        return new InvestmentResponse(
                investment.getId(),
                investment.getName(),
                investment.getType(),
                investment.getDescription(),
                investment.getFinancialGoalId(),
                investment.isActive(),
                totalContributed != null ? totalContributed : BigDecimal.ZERO,
                investment.getCreatedAt(),
                investment.getUpdatedAt()
        );
    }
}