package org.store.api.service;

import org.store.api.entity.Category;
import org.store.api.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Get all categories
    public List<Category> getAllCategories() throws Exception {
        return categoryRepository.findAll();
    }

    // Get a category by ID
    public Optional<Category> getCategoryById(Long id) throws Exception {
        return categoryRepository.findById(id);
    }

    // Create or update a category
    public void saveCategory(Category category) throws Exception {
        categoryRepository.save(category);
    }

    // Delete a category by ID
    public void deleteCategory(Long id) throws Exception {
        categoryRepository.deleteById(id);
    }
}