package com.financas.repository;

import com.financas.domain.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditCardRepository
        extends JpaRepository<CreditCard, Long> {

    List<CreditCard> findByUserIdAndActiveTrue(Long userId);

    Optional<CreditCard> findByIdAndUserId(Long id, Long userId);
}