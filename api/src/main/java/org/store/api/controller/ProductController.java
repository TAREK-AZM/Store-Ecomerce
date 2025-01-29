package org.store.api.controller;

import org.store.api.entity.Product;
import org.store.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URLDecoder;
import java.util.Collections;
import java.nio.charset.StandardCharsets; // For specifying character encoding


@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Get all products
    @GetMapping("all")
    public List<Product> getAllProducts() throws Exception {
        return productService.getAllProducts();
    }

    // Get products by XPath expression
    @GetMapping("/xpath/{xpathExpression}")
    public ResponseEntity<List<Product>> getProductsByXPath(
            @PathVariable String xpathExpression) throws Exception {
        try {
            // Decode the URL-encoded XPath expression
            String decodedExpression = URLDecoder.decode(xpathExpression, StandardCharsets.UTF_8.toString());
            List<Product> products = productService.getAllProductsXpath(decodedExpression);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.emptyList());
        }
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