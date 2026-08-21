package com.financas.repository;

import com.financas.domain.CreditCardInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CreditCardInvoiceRepository extends JpaRepository<CreditCardInvoice, Long> {
    Optional<CreditCardInvoice> findByCreditCardIdAndReferenceMonth(Long creditCardId, LocalDate referenceMonth);
    List<CreditCardInvoice> findByCreditCardIdOrderByReferenceMonthDesc(Long creditCardId);
}
