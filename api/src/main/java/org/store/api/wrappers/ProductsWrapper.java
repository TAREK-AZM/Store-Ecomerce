package org.store.api.entity;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "products") // Root element for XML
public class ProductsWrapper {
    private List<Product> products;

    @XmlElement(name = "product") // Each item in the list is a <product> element
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}