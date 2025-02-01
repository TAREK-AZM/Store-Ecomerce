package org.store.api.entity;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "categories") // Root element for XML
public class CategoriesWrapper {
    private List<Category> categories;

    @XmlElement(name = "category") // Each item in the list is a <category> element
    public List<Category> getCategories() { return categories; }
    public void setCategories(List<Category> categories) { this.categories = categories; }
}