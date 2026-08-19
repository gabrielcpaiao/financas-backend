package com.financas.domain;

import com.financas.domain.enums.CategoryContext;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Espelha a tabela `category` do schema-financial-control.sql.
// Sirva de referência para modelar as demais entidades (account,
// financial_transaction, credit_card, investment, planned_purchase...).
@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 7)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryContext context;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}
