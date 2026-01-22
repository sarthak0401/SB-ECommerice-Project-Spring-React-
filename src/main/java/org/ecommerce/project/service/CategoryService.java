package org.ecommerce.project.service;

import org.ecommerce.project.payload.CategoryDTO;
import org.ecommerce.project.payload.CategoryResponse;

public interface CategoryService {
    CategoryResponse getAllCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(long categoryId);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, long categoryId);
}
