package com.financas.domain;

import com.financas.domain.enums.PaymentMethod;
import com.financas.domain.enums.PlannedPurchasePriority;
import com.financas.domain.enums.PlannedPurchaseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "planned_purchase")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlannedPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "item_name", nullable = false, length = 150)
    private String itemName;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "expected_payment_method")
    private PaymentMethod expectedPaymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlannedPurchasePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlannedPurchaseStatus status;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "credit_card_purchase_id")
    private Long creditCardPurchaseId;

    @Column(length = 255)
    private String notes;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}