package com.financas.service.impl;

import com.financas.domain.Category;
import com.financas.dto.request.CategoryRequest;
import com.financas.dto.response.CategoryResponse;
import com.financas.exception.ResourceNotFoundException;
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
        return categoryRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse create(Long userId, CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        category.setUserId(userId);
        category.setActive(true);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse update(Long userId, Long id, CategoryRequest request) {
        Category category = findOwnedOrThrow(userId, id);
        category.setName(request.name());
        category.setColor(request.color());
        category.setContext(request.context());
        category.setParentId(request.parentId());
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public void deactivate(Long userId, Long id) {
        // Nunca DELETE de verdade: categoria pode estar ligada a lançamentos
        // históricos (mesmo raciocínio do ON DELETE RESTRICT do schema).
        Category category = findOwnedOrThrow(userId, id);
        category.setActive(false);
        categoryRepository.save(category);
    }

    private Category findOwnedOrThrow(Long userId, Long id) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));
    }
}
