package org.store.api.entity;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "product")
@XmlType(propOrder = {"id", "title", "description", "price","stockQuantity","imageUrl","category"})

public class Product {

    private Long id;
    private String title;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private String imageUrl;
    private String category; // Reference to the category by ID

    // Getters with @XmlElement annotations
    @XmlElement
    public Long getId() { return id; }

    @XmlElement
    public String getTitle() { return title; }

    @XmlElement
    public String getDescription() { return description; }

    @XmlElement
    public Double getPrice() { return price; }

    @XmlElement
    public Integer getStockQuantity() { return stockQuantity; }

    @XmlElement
    public String getImageUrl() { return imageUrl; }

    @XmlElement
    public String getCategory() { return category; }
}