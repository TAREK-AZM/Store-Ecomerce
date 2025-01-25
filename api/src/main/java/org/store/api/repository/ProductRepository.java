package org.store.api.repository;

import org.store.api.entity.Product;
import org.store.api.entity.ProductsWrapper;
import org.store.api.util.XmlUtil;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    private static final String PRODUCTS_FILE = "src/main/resources/data/products.xml";

    // Read all products from the XML file
    public List<Product> findAll() throws Exception {
        ProductsWrapper wrapper = XmlUtil.readXml(PRODUCTS_FILE, ProductsWrapper.class);
        return wrapper.getProducts();
    }

    // Find a product by ID
    public Optional<Product> findById(Long id) throws Exception {
        return findAll().stream()
                .filter(product -> product.getId().equals(id))
                .findFirst();
    }

    // Save a product (add or update)
    public void save(Product product) throws Exception {
        List<Product> products = findAll();
        products.removeIf(p -> p.getId().equals(product.getId())); // Remove existing product if it exists
        products.add(product); // Add the new/updated product
        ProductsWrapper wrapper = new ProductsWrapper();
        wrapper.setProducts(products);
        XmlUtil.writeXml(PRODUCTS_FILE, wrapper);
    }

    // Delete a product by ID
    public void deleteById(Long id) throws Exception {
        List<Product> products = findAll();
        products.removeIf(product -> product.getId().equals(id)); // Remove the product
        ProductsWrapper wrapper = new ProductsWrapper();
        wrapper.setProducts(products);
        XmlUtil.writeXml(PRODUCTS_FILE, wrapper);
    }
}