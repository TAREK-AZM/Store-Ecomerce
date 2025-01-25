package org.store.api.controller;

import org.store.api.entity.Product;
import org.store.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Get all products
    @GetMapping
    public List<Product> getAllProducts() throws Exception {
        return productService.getAllProducts();
    }

    // Get a product by ID
    @GetMapping("/{id}")
    public Optional<Product> getProductById(@PathVariable Long id) throws Exception {
        return productService.getProductById(id);
    }

    // Create or update a product
    @PostMapping
    public void saveProduct(@RequestBody Product product) throws Exception {
        productService.saveProduct(product);
    }

    // Delete a product by ID
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) throws Exception {
        productService.deleteProduct(id);
    }
}