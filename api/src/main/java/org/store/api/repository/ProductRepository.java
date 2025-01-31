package org.store.api.repository;

import org.store.api.entity.Product;
import org.store.api.entity.ProductsWrapper;
import org.store.api.util.XmlUtil;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ArrayList;
import java.io.FileNotFoundException;  // Ensure this import is present
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.store.api.util.IdGenerator;


// XML parsing
import org.w3c.dom.Document; // To represent the XML document
import org.w3c.dom.NodeList; // To store the nodes matching the XPath expression
import org.w3c.dom.Node; // To store the nodes matching the XPath expression

// XPath processing
import javax.xml.xpath.XPath; // To create XPath objects
import javax.xml.xpath.XPathConstants; // XPath constants for evaluating expressions
import javax.xml.xpath.XPathExpression; // To compile and evaluate XPath expressions
import javax.xml.xpath.XPathFactory; // To create instances of XPath

// XML document builder
import javax.xml.parsers.DocumentBuilder; // To parse XML documents
import javax.xml.parsers.DocumentBuilderFactory; // To create DocumentBuilder instances

// Exception handling
import javax.xml.parsers.ParserConfigurationException; // For parsing errors
import org.xml.sax.SAXException; // For handling SAX parsing errors

// File handling
import java.io.File; // For reading the XML file
import java.io.FileInputStream;  // Add this line

//import java.io.FileInputStreanm;

@Repository
public class ProductRepository {

    private static final String PRODUCTS_FILE = "src/main/resources/data/products.xml";
    private static final String PRODUCTS_XSD = "src/main/resources/data/products.xsd";

    // Read all products from the XML file
    public List<Product> findAll() throws Exception {
        ProductsWrapper wrapper = XmlUtil.readXml(PRODUCTS_FILE, ProductsWrapper.class,PRODUCTS_XSD);
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
        product.setId(IdGenerator.generateUniqueId(findAll(), Product::getId));
        ProductsWrapper wrapper = new ProductsWrapper();
        wrapper.setProducts(products);
        XmlUtil.writeXml(PRODUCTS_FILE, wrapper,PRODUCTS_XSD);
    }

    // Delete a product by ID
    public void deleteById(Long id) throws Exception {
        List<Product> products = findAll();
        products.removeIf(product -> product.getId().equals(id)); // Remove the product
        ProductsWrapper wrapper = new ProductsWrapper();
        wrapper.setProducts(products);
        XmlUtil.writeXml(PRODUCTS_FILE, wrapper,PRODUCTS_XSD);
    }


         // Find products by XPath expression
         // Enhanced findByXPath method supporting complex queries
         public List<Product> findByXPath(String xpathExpression) throws Exception {
             List<Product> results = new ArrayList<>();
             File xmlFile = new File(PRODUCTS_FILE);

             try {
                 // Create DocumentBuilder with namespace awareness
                 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                 factory.setNamespaceAware(true);
                 DocumentBuilder builder = factory.newDocumentBuilder();

                 // Parse XML file
                 Document doc = builder.parse(new FileInputStream(xmlFile));

                 // Create XPath
                 XPathFactory xPathFactory = XPathFactory.newInstance();
                 XPath xpath = xPathFactory.newXPath();

                 // Process the XPath expression based on the query type
                 String processedXPath = processXPathExpression(xpathExpression);

                 // Evaluate XPath expression
                 XPathExpression expr = xpath.compile(processedXPath);
                 NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

                 // helper method to Process matching nodes
                 results = convertNodesToProducts(nodes);
             } catch (Exception e) {
                 throw new Exception("Error searching products by XPath: " + e.getMessage());
             }

             return results;
         }


    // Helper method to process XPath expressions
//    private String processXPathExpression(String rawExpression) {
//        // If it's already a complex XPath expression, return as is
//        if (rawExpression.startsWith("//") || rawExpression.contains("[") || rawExpression.contains("@")) {
//            return rawExpression;
//        }
//
//        // Check if it's a price range format (price_range:min-max)
//        if (rawExpression.startsWith("price_range:")) {
//            String[] range = rawExpression.split(":")[1].split("-");
//            if (range.length == 2) {
//                return String.format("//product[price >= %s and price <= %s]", range[0], range[1]);
//            }
//        }
//
//        // Check if it's a stock threshold format (stock_gt:value)
//        if (rawExpression.startsWith("stock_gt:")) {
//            String threshold = rawExpression.split(":")[1];
//            System.out.println("------>threshold<-------"+threshold);
//            return String.format("//product[stockQuantity > 10]");
//        }
//        String lowerExpression = rawExpression.toLowerCase().trim();
//
//        return String.format(
//                "//product[contains(translate(category, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s') " +
//                        "or contains(translate(title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s') " +
//                        "or contains(translate(description, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')]",
//                lowerExpression, lowerExpression, lowerExpression
//        );
//    }


    private String processXPathExpression(String rawExpression) {
        // If it's already a complex XPath expression, return as is
        if (rawExpression.startsWith("//") || rawExpression.contains("[") || rawExpression.contains("@")) {
            return rawExpression;
        }

        // Check if it's a price range format (price_range:min-max)
        if (rawExpression.startsWith("price_range:")) {
            String[] range = rawExpression.split(":")[1].split("-");
            if (range.length == 2) {
                return String.format("//product[price >= %s and price <= %s]", range[0], range[1]);
            }
        }

        // Check if it's a stock threshold format (stock_gt:value)
        if (rawExpression.startsWith("stock_gt:")) {
            String threshold = rawExpression.split(":")[1];
            System.out.println("------>threshold<-------" + threshold);
            return String.format("//product[stockQuantity > 10]");
        }

        // Split the rawExpression into individual words
        String[] words = rawExpression.split("\\s+"); // Split by whitespace
        StringBuilder xpathBuilder = new StringBuilder("//product[");

        // Add conditions for each word
        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase(); // Convert word to lowercase for case-insensitive search
            if (i > 0) {
                xpathBuilder.append(" and ");
            }
            xpathBuilder.append(
                    "(contains(translate(category, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + word + "') or " +
                            "contains(translate(title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + word + "') or " +
                            "contains(translate(description, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + word + "'))"
            );
        }

        xpathBuilder.append("]");
        return xpathBuilder.toString();
    }

    // Helper method to convert XML nodes to Product objects
    private List<Product> convertNodesToProducts(NodeList nodes) {
        List<Product> products = new ArrayList<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Product product = new Product();
                NodeList childNodes = node.getChildNodes();

                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node childNode = childNodes.item(j);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        String content = childNode.getTextContent();
                        setProductField(product, childNode.getNodeName(), content);
                    }
                }
                products.add(product);
            }
        }
        return products;
    }

    // Helper method to set product fields
    private void setProductField(Product product, String fieldName, String value) {
        try {
            switch (fieldName) {
                case "id":
                    product.setId(Long.parseLong(value));
                    break;
                case "title":
                    product.setTitle(value);
                    break;
                case "description":
                    product.setDescription(value);
                    break;
                case "price":
                    product.setPrice(Double.parseDouble(value));
                    break;
                case "stockQuantity":
                    product.setStockQuantity(Integer.parseInt(value));
                    break;
                case "imageUrl":
                    product.setImageUrl(value);
                    break;
                case "category":
                    product.setCategory(value);
                    break;
            }
        } catch (NumberFormatException e) {
            // Log error but continue processing
            System.err.println("Error parsing value for field " + fieldName + ": " + value);
        }
    }


}
