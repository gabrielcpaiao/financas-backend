package com.financas.repository;

import com.financas.domain.CreditCardInstallment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditCardInstallmentRepository extends JpaRepository<CreditCardInstallment, Long> {
    List<CreditCardInstallment> findByInvoiceId(Long invoiceId);
    List<CreditCardInstallment> findByCreditCardPurchaseIdOrderByInstallmentNumber(Long creditCardPurchaseId);
}
