package com.financas.repository;

import com.financas.domain.CreditCardPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditCardPurchaseRepository extends JpaRepository<CreditCardPurchase, Long> {
    List<CreditCardPurchase> findByUserIdAndCreditCardIdOrderByPurchaseDateDescIdDesc(Long userId, Long creditCardId);
    Optional<CreditCardPurchase> findByIdAndUserId(Long id, Long userId);
}
