package org.store.api.controller;

import org.store.api.entity.Category;
import org.store.api.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Get all categories
    @GetMapping
    public List<Category> getAllCategories() throws Exception {
        return categoryService.getAllCategories();
    }

    // Get a category by ID
    @GetMapping("/{id}")
    public Optional<Category> getCategoryById(@PathVariable Long id) throws Exception {
        return categoryService.getCategoryById(id);
    }

    // Create or update a category
    @PostMapping
    public void saveCategory(@RequestBody Category category) throws Exception {
        categoryService.saveCategory(category);
    }

    // Delete a category by ID
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) throws Exception {
        categoryService.deleteCategory(id);
    }
}