package com.example.shopapp.services;

import com.example.shopapp.dtos.CategoryDTO;
import com.example.shopapp.models.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO category);
    Category getCategoryById(Long id);

    List<Category> getAllCategories();
    Category updateCategory(long id, CategoryDTO category);
    void deleteCategory(long id);
}
