package com.financas.repository;

import com.financas.domain.PlannedPurchase;
import com.financas.domain.enums.PlannedPurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlannedPurchaseRepository extends JpaRepository<PlannedPurchase, Long> {
    List<PlannedPurchase> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<PlannedPurchase> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, PlannedPurchaseStatus status);
    Optional<PlannedPurchase> findByIdAndUserId(Long id, Long userId);
}