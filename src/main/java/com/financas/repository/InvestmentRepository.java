package com.financas.repository;

import com.financas.domain.Investment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvestmentRepository
        extends JpaRepository<Investment, Long> {

    List<Investment> findByUserIdAndActiveTrue(Long userId);

    Optional<Investment> findByIdAndUserId(
            Long id,
            Long userId
    );

    Optional<Investment> findByUserIdAndNameIgnoreCaseAndActiveTrue(
            Long userId,
            String name
    );
}