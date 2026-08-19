package com.financas.repository;

import com.financas.domain.Category;
import com.financas.domain.enums.CategoryContext;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserIdAndActiveTrue(Long userId);

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    Optional<Category> findByUserIdAndNameIgnoreCaseAndContext(
            Long userId,
            String name,
            CategoryContext context
    );

    boolean existsByParentIdAndActiveTrue(Long parentId);
}