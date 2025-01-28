package org.store.api.repository;

import org.store.api.entity.Category;
import org.store.api.entity.CategoriesWrapper;
import org.store.api.util.XmlUtil;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepository {

    private static final String CATEGORIES_FILE = "src/main/resources/data/categories.xml";
    private static final String CATEGORIES_XSD = "src/main/resources/data/categories.xsd";

    // Read all categories from the XML file
    public List<Category> findAll() throws Exception {
        CategoriesWrapper wrapper = XmlUtil.readXml(CATEGORIES_FILE, CategoriesWrapper.class,CATEGORIES_XSD);
        return wrapper.getCategories();
    }

    // Find a category by ID
    public Optional<Category> findById(Long id) throws Exception {
        return findAll().stream()
                .filter(category -> category.getId().equals(id))
                .findFirst();
    }

    // Save a category (add or update)
    public void save(Category category) throws Exception {
        List<Category> categories = findAll();
        categories.removeIf(c -> c.getId().equals(category.getId())); // Remove existing category if it exists
        categories.add(category); // Add the new/updated category
        CategoriesWrapper wrapper = new CategoriesWrapper();
        wrapper.setCategories(categories);
        XmlUtil.writeXml(CATEGORIES_FILE, wrapper,CATEGORIES_XSD);
    }

    // Delete a category by ID
    public void deleteById(Long id) throws Exception {
        List<Category> categories = findAll();
        categories.removeIf(category -> category.getId().equals(id)); // Remove the category
        CategoriesWrapper wrapper = new CategoriesWrapper();
        wrapper.setCategories(categories);
        XmlUtil.writeXml(CATEGORIES_FILE, wrapper,CATEGORIES_XSD);
    }
}