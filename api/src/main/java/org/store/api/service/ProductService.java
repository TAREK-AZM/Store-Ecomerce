package org.store.api.service;

import org.store.api.entity.Product;
import org.store.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Get all products
    public List<Product> getAllProducts() throws Exception {
        return productRepository.findAll();
    }

    // Get all products xpath feltered
    public List<Product> getAllProductsXpath(String xpathExpression) throws Exception {
        return productRepository.findByXPath( xpathExpression);
    }

    // Get a product by ID
    public Optional<Product> getProductById(Long id) throws Exception {
        return productRepository.findById(id);
    }

    // Create or update a product
    public void saveProduct(Product product) throws Exception {
        productRepository.save(product);
    }

    // Delete a product by ID
    public void deleteProduct(Long id) throws Exception {
        productRepository.deleteById(id);
    }
}