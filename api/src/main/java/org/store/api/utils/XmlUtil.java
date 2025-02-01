package org.store.api.util;

import jakarta.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlUtil {

    // Read XML with validation
    public static <T> T readXml(String xmlPath, Class<T> clazz, String xsdPath)
            throws JAXBException, SAXException, FileNotFoundException {

        File file = new File(xmlPath);
        if (!file.exists()) {
            throw new FileNotFoundException("XML file not found: " + xmlPath);
        }
        if (file.length() == 0) {
            throw new SAXParseException("XML file is empty: " + xmlPath, null);
        }

        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        // Load XSD and enforce validation
        Schema schema = loadSchema(xsdPath);
        unmarshaller.setSchema(schema);

        // Add validation event handler
        unmarshaller.setEventHandler(new ValidationEventHandler() {
            @Override
            public boolean handleEvent(ValidationEvent event) {
                // Log validation errors
                System.err.println("Validation error: " + event.getMessage());
                return false; // Continue after validation errors
            }
        });

        return clazz.cast(unmarshaller.unmarshal(file));
    }

    // Write XML with validation
    public static <T> void writeXml(String xmlPath, T object, String xsdPath)
            throws JAXBException, SAXException {

        JAXBContext context = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = context.createMarshaller();

        // Load XSD and enforce validation
        Schema schema = loadSchema(xsdPath);
        marshaller.setSchema(schema);

        // Add validation event handler
        marshaller.setEventHandler(new ValidationEventHandler() {
            @Override
            public boolean handleEvent(ValidationEvent event) {
                // Log validation errors
                System.err.println("Validation error: " + event.getMessage());
                return false; // Continue after validation errors
            }
        });

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        // First validate by marshalling to a StringWriter
        StringWriter sw = new StringWriter();
        marshaller.marshal(object, sw);

        // If validation passes, write to file
        marshaller.marshal(object, new File(xmlPath));
    }

    // Helper method to load the XSD schema
    private static Schema loadSchema(String xsdPath) throws SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        File schemaFile = new File(xsdPath);
        if (!schemaFile.exists()) {
            throw new SAXException("XSD schema file not found: " + xsdPath);
        }
        return schemaFactory.newSchema(schemaFile);
    }
}