package com.financas.service.impl;

import com.financas.domain.Category;
import com.financas.dto.request.CategoryRequest;
import com.financas.dto.response.CategoryResponse;
import com.financas.exception.BusinessRuleException;
import com.financas.exception.ResourceNotFoundException;
import com.financas.exception.ConflictException;
import com.financas.mapper.CategoryMapper;
import com.financas.repository.CategoryRepository;
import com.financas.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> listActive(Long userId) {
        return categoryRepository.findByUserIdAndActiveTrue(userId)
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse create(Long userId, CategoryRequest request) {
        validateParent(userId, null, request);
        validateDuplicateName(userId, null, request);

        Category category = categoryMapper.toEntity(request);
        category.setUserId(userId);
        category.setActive(true);

        return categoryMapper.toResponse(
                categoryRepository.save(category)
        );
    }

    @Override
    public CategoryResponse update(
            Long userId,
            Long id,
            CategoryRequest request
    ) {
        Category category = findOwnedOrThrow(userId, id);

        validateParent(userId, id, request);
        validateDuplicateName(userId, id, request);

        category.setName(request.name());
        category.setColor(request.color());
        category.setContext(request.context());
        category.setParentId(request.parentId());

        return categoryMapper.toResponse(
                categoryRepository.save(category)
        );
    }

    @Override
    public void deactivate(Long userId, Long id) {
        Category category = findOwnedOrThrow(userId, id);

        if (categoryRepository.existsByParentIdAndActiveTrue(id)) {
            throw BusinessRuleException.categoryHasActiveChildren();
        }

        category.setActive(false);
        categoryRepository.save(category);
    }

    private Category findOwnedOrThrow(Long userId, Long id) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Categoria não encontrada: " + id
                        )
                );
    }

    private void validateParent(
            Long userId,
            Long categoryId,
            CategoryRequest request
    ) {
        if (request.parentId() == null) {
            return;
        }

        if (categoryId != null &&
                categoryId.equals(request.parentId())) {
            throw BusinessRuleException.categoryCannotBeItsOwnParent();
        }

        Category parent = categoryRepository
                .findByIdAndUserId(request.parentId(), userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Categoria pai não encontrada: "
                                        + request.parentId()
                        )
                );

        if (!parent.isActive()) {
            throw BusinessRuleException.parentCategoryInactive();
        }

        if (parent.getContext() != request.context()) {
            throw BusinessRuleException.parentContextMismatch();
        }
    }

    private void validateDuplicateName(
            Long userId,
            Long categoryId,
            CategoryRequest request
    ) {
        categoryRepository
                .findByUserIdAndNameIgnoreCaseAndContext(
                        userId,
                        request.name(),
                        request.context()
                )
                .ifPresent(existing -> {
                    if (categoryId == null ||
                            !existing.getId().equals(categoryId)) {
                        throw ConflictException.duplicateCategoryName(
                                request.name()
                        );
                    }
                });
    }
}