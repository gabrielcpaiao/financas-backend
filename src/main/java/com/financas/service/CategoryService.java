package com.financas.service;

import com.financas.dto.request.CategoryRequest;
import com.financas.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> listActive(Long userId);

    CategoryResponse create(Long userId, CategoryRequest request);

    CategoryResponse update(Long userId, Long id, CategoryRequest request);

    void deactivate(Long userId, Long id);
}