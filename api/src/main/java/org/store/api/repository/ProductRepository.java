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

    // // Find products by an XPath expression
//        public List<Product> findByXPath(String xpathExpression) throws Exception {
//            List<Product> filteredProducts = new ArrayList<>();
//
//            // Initialize XML parsing
//            File xmlFile = new File(PRODUCTS_FILE);
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            Document doc = dBuilder.parse(xmlFile);
//
//            // Create XPath object
//            XPathFactory xPathFactory = XPathFactory.newInstance();
//            XPath xpath = xPathFactory.newXPath();
//
//            String xpathExpression_t = "/products/product/category[text()='"+xpathExpression+"']";
//
//            // Evaluate XPath expression
//            XPathExpression expr = xpath.compile(xpathExpression_t);
//            System.out.println("<----compile the Xpath arived-------->");
//            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
//
//            // Iterate through the matching nodes and extract their IDs
//            for (int i = 0; i < nodeList.getLength(); i++) {
//                String productIdString = nodeList.item(i).getTextContent();
//                Long productId = Long.parseLong(productIdString);
//
//                // Use the product ID to find the corresponding Product object
//                Optional<Product> productOpt = findById(productId);
//                productOpt.ifPresent(filteredProducts::add);
//            }
//
//            return filteredProducts;
//        }

    // Find products by an XPath expression
    public List<Product> findByXPath(String xpathExpression) throws Exception {
        List<Product> filteredProducts = new ArrayList<>();

        // Initialize XML parsing using getResourceAsStream() to load the file
        FileInputStream xmlInputStream = getClass().getClassLoader().getResourceAsStream(PRODUCTS_FILE);
        if (xmlInputStream == null) {
            throw new FileNotFoundException("XML file not found in classpath.");
        }

        // Parse the document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlInputStream);

        // Log document parsing success
        System.out.println("@@@@@@@@@@@@@@@@@2the parsed file " + doc);

        // Create XPath object
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();

        // Compile and evaluate the XPath expression
        XPathExpression expr = xpath.compile(xpathExpression);
        System.out.println("<----compiled the XPath expression-------->");
        NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        // Log number of matching nodes
        System.out.println("==========product======"+nodeList+" Length: " + nodeList.getLength());

        // Iterate through the matching nodes and extract the product elements
        for (int i = 0; i < nodeList.getLength(); i++) {
            // Get the entire <product> element (the parent node of <category>)
            Node productNode = nodeList.item(i).getParentNode(); // Get the parent <product> element
            System.out.println("==========product======"+productNode.getNodeName());

            // Now, extract the product ID from the <product> element
            NodeList childNodes = productNode.getChildNodes();
            String productIdString = "";
            for (int j = 0; j < childNodes.getLength(); j++) {
                if ("id".equals(childNodes.item(j).getNodeName())) {
                    productIdString = childNodes.item(j).getTextContent();
                    break;
                }
            }

            if (!productIdString.isEmpty()) {
                Long productId = Long.parseLong(productIdString); // Parse the product ID

                // Use the product ID to find the corresponding Product object
                Optional<Product> productOpt = findById(productId);
                productOpt.ifPresent(filteredProducts::add);
            }
        }

        return filteredProducts;
    }

}
