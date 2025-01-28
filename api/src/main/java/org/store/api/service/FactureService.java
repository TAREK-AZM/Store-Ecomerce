package org.store.api.service;

import org.store.api.entity.Facture;
import org.store.api.entity.Command;
import org.store.api.entity.LineCommand;
import org.store.api.entity.Product;
import org.store.api.repository.FactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.xhtmlrenderer.pdf.ITextRenderer;


import java.io.FileNotFoundException;

@Service
public class FactureService {

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private ProductService productService;

    public Facture createFacture(Command command, List<LineCommand> lineCommands) throws Exception {
        double totalAmount = calculateTotalAmount(lineCommands);

        Facture facture = new Facture();
        facture.setId(generateFactureId());
        facture.setCommandId(command.getId());
        facture.setUserId(command.getUserId());
        facture.setInvoiceDate(LocalDate.now().toString());
        facture.setTotalAmount(totalAmount);
        facture.setStatus("CREATED");
        facture.setLines(lineCommands);

        factureRepository.save(facture);
        generatePdf(facture);

        return facture;
    }

    private double calculateTotalAmount(List<LineCommand> lineCommands) throws Exception {
        double total = 0;
        for (LineCommand line : lineCommands) {
            Product product = productService.getProductById(line.getProductId())
                    .orElseThrow(() -> new Exception("Product not found"));
            total += product.getPrice() * line.getQuantity();
        }
        return total;
    }

    private Long generateFactureId() {
        return System.currentTimeMillis();
    }

    public void generatePdf(Facture facture) throws Exception {
        // Convert Facture to XML
        String xmlContent = convertFactureToXml(facture);
        File xmlFile = new File("factures.xml");
        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(xmlContent);
        }

        // Transform XML to HTML using XSLT
        transformXMLToHTML("factures.xml", "factures.xslt", "invoice.html");

        // Convert HTML to PDF
        HtmlToPdf.convertHtmlToPdf("invoice.html", "src/main/resources/factures/facture_" + facture.getId() + ".pdf");
    }

    private String convertFactureToXml(Facture facture) throws Exception {
        StringBuilder xml = new StringBuilder();
        xml.append("<invoice>");
        xml.append("<id>").append(facture.getId()).append("</id>");
        xml.append("<date>").append(facture.getInvoiceDate()).append("</date>");
        xml.append("<commandId>").append(facture.getCommandId()).append("</commandId>");
        xml.append("<userId>").append(facture.getUserId()).append("</userId>");
        xml.append("<totalAmount>").append(facture.getTotalAmount()).append("</totalAmount>");
        xml.append("<lines>");
        for (LineCommand line : facture.getLines()) {
            xml.append("<line>");
            xml.append("<product>").append(line.getProductId()).append("</product>");
            xml.append("<quantity>").append(line.getQuantity()).append("</quantity>");
            xml.append("<unitPrice>").append(productService.getProductById(line.getProductId()).get().getPrice()).append("</unitPrice>");
            xml.append("<total>").append(line.getQuantity() * productService.getProductById(line.getProductId()).get().getPrice()).append("</total>");
            xml.append("</line>");
        }
        xml.append("</lines>");
        xml.append("</invoice>");
        return xml.toString();
    }

    public Optional<Facture> getFactureById(Long id) {
        return factureRepository.findById(id);
    }

    public List<Facture> getAllFactures() {
        return factureRepository.findAll();
    }

    public List<Facture> getUserFactures(Long userId) {
        return factureRepository.findByUserId(userId);
    }

    // Helper method for XML to HTML transformation
    public static void transformXMLToHTML(String xmlPath, String xsltPath, String outputHtmlPath) throws Exception {
        // Use the classpath to locate the XSLT file in the resources/data folder
        InputStream xsltStream = FactureService.class.getClassLoader().getResourceAsStream("data/factures.xslt");
        if (xsltStream == null) {
            throw new FileNotFoundException("XSLT file not found: " + xsltPath);
        }

        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(xsltStream);
        Transformer transformer = factory.newTransformer(xslt);

        Source xml = new StreamSource(new File(xmlPath));
        transformer.transform(xml, new StreamResult(new FileOutputStream(outputHtmlPath)));
    }

    // Helper class for HTML to PDF conversion
    public static class HtmlToPdf {
        public static void convertHtmlToPdf(String htmlPath, String pdfPath) throws Exception {
            try (OutputStream os = new FileOutputStream(pdfPath)) {
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocument(new File(htmlPath));
                renderer.layout();
                renderer.createPDF(os);
            }
        }
    }
}
