package com.financas.service.impl;

import com.financas.domain.FinancialGoal;
import com.financas.domain.Investment;
import com.financas.dto.request.FinancialGoalRequest;
import com.financas.dto.response.FinancialGoalResponse;
import com.financas.exception.ResourceNotFoundException;
import com.financas.mapper.FinancialGoalMapper;
import com.financas.repository.FinancialGoalRepository;
import com.financas.repository.InvestmentContributionRepository;
import com.financas.repository.InvestmentRepository;
import com.financas.service.FinancialGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialGoalServiceImpl implements FinancialGoalService {

    private final FinancialGoalRepository financialGoalRepository;
    private final InvestmentRepository investmentRepository;
    private final InvestmentContributionRepository investmentContributionRepository;
    private final FinancialGoalMapper financialGoalMapper;

    @Override
    public List<FinancialGoalResponse> listActive(Long userId) {
        return financialGoalRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(goal -> toResponse(goal, totalContributedToGoal(userId, goal.getId())))
                .toList();
    }

    @Override
    public FinancialGoalResponse create(Long userId, FinancialGoalRequest request) {
        FinancialGoal goal = financialGoalMapper.toEntity(request);
        goal.setUserId(userId);
        goal.setActive(true);
        goal = financialGoalRepository.save(goal);
        return toResponse(goal, BigDecimal.ZERO);
    }

    @Override
    public FinancialGoalResponse update(Long userId, Long id, FinancialGoalRequest request) {
        FinancialGoal goal = findOwnedOrThrow(userId, id);
        goal.setName(request.name());
        goal.setTargetAmount(request.targetAmount());
        goal.setTargetDate(request.targetDate());
        goal = financialGoalRepository.save(goal);
        return toResponse(goal, totalContributedToGoal(userId, goal.getId()));
    }

    @Override
    public void deactivate(Long userId, Long id) {
        // Nunca DELETE de verdade: meta pode estar referenciada por investment
        // (ON DELETE SET NULL no schema, mas o histórico de aportes continua valendo).
        FinancialGoal goal = findOwnedOrThrow(userId, id);
        goal.setActive(false);
        financialGoalRepository.save(goal);
    }

    private BigDecimal totalContributedToGoal(Long userId, Long goalId) {
        // Não há coluna financial_goal_id em investment_contribution — o vínculo
        // é indireto via investment. Soma-se o total de cada investimento ligado à meta.
        List<Investment> investments = investmentRepository.findByUserIdAndActiveTrue(userId).stream()
                .filter(i -> goalId.equals(i.getFinancialGoalId()))
                .toList();

        return investments.stream()
                .map(investment -> investmentContributionRepository.sumAmountByInvestmentId(investment.getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private FinancialGoal findOwnedOrThrow(Long userId, Long id) {
        return financialGoalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Objetivo não encontrado: " + id));
    }

    private FinancialGoalResponse toResponse(FinancialGoal goal, BigDecimal totalContributed) {
        return new FinancialGoalResponse(
                goal.getId(),
                goal.getName(),
                goal.getTargetAmount(),
                goal.getTargetDate(),
                goal.getActive(),
                totalContributed
        );
    }
}