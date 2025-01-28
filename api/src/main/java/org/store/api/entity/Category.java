package org.store.api.entity;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@XmlRootElement(name = "category") // Root element for XML
@XmlType(propOrder = {"id", "name", "description", "imageUrl"})

public class Category {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;

    // Getters with @XmlElement annotations
    @XmlElement
    public Long getId() { return id; }

    @XmlElement
    public String getName() { return name; }

    @XmlElement
    public String getDescription() { return description; }

    @XmlElement
    public String getImageUrl() { return imageUrl; }
}