package org.store.api.service;

import org.store.api.entity.Facture;
import org.store.api.entity.Command;
import org.store.api.entity.LineCommand;
import org.store.api.entity.Product;
import org.store.api.repository.FactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;  // Added this import
@Service
public class FactureService {

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private ProductService productService;

    private static final String PDF_DIRECTORY = "src/main/resources/factures/";
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

    public Facture createFacture(Command command, List<LineCommand> lineCommands) throws Exception {
        // Calculate total amount
        double totalAmount = calculateTotalAmount(lineCommands);

        // Create new facture
        Facture facture = new Facture();
        facture.setId(generateFactureId());
        facture.setCommandId(command.getId());
        facture.setUserId(command.getUserId());
        facture.setInvoiceDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        facture.setTotalAmount(totalAmount);
        facture.setStatus("CREATED");

        // Save facture to XML
        factureRepository.save(facture);

        // Generate PDF
        generatePdf(facture, lineCommands);

        return facture;
    }

    private double calculateTotalAmount(List<LineCommand> lineCommands) throws Exception {
        double total = 0;
        for (LineCommand line : lineCommands) {
            Product product = productService.getProductById(line.getProductId())
                    .orElseThrow(() -> new Exception("Product not found: " + line.getProductId()));
            total += product.getPrice() * line.getQuantity();
        }
        return Math.round(total * 100.0) / 100.0; // Round to 2 decimal places
    }

    private Long generateFactureId() {
        return System.currentTimeMillis();
    }

    public void generatePdf(Facture facture, List<LineCommand> lineCommands) {
        try {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            String pdfPath = PDF_DIRECTORY + "facture_" + facture.getId() + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(pdfPath));

            document.open();

            // Add company logo/header
            Paragraph header = new Paragraph("COMPANY NAME", TITLE_FONT);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(Chunk.NEWLINE);

            // Add invoice details
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);

            addInfoCell(infoTable, "Invoice Number:", facture.getId().toString());
            addInfoCell(infoTable, "Date:", facture.getInvoiceDate());
            addInfoCell(infoTable, "Order Reference:", facture.getCommandId().toString());
            addInfoCell(infoTable, "Customer ID:", facture.getUserId().toString());
            document.add(infoTable);
            document.add(Chunk.NEWLINE);

            // Add items table
            PdfPTable itemsTable = new PdfPTable(new float[]{4, 1, 2, 2});
            itemsTable.setWidthPercentage(100);

            // Add table headers
            addTableHeader(itemsTable);

            // Add line items
            double subtotal = 0;
            for (LineCommand line : lineCommands) {
                Product product = productService.getProductById(line.getProductId())
                        .orElseThrow(() -> new Exception("Product not found"));
                double lineTotal = product.getPrice() * line.getQuantity();
                subtotal += lineTotal;

                itemsTable.addCell(new PdfPCell(new Phrase(product.getTitle(), NORMAL_FONT)));
                itemsTable.addCell(new PdfPCell(new Phrase(String.valueOf(line.getQuantity()), NORMAL_FONT)));
                itemsTable.addCell(new PdfPCell(new Phrase(String.format("$%.2f", product.getPrice()), NORMAL_FONT)));
                itemsTable.addCell(new PdfPCell(new Phrase(String.format("$%.2f", lineTotal), NORMAL_FONT)));
            }

            document.add(itemsTable);
            document.add(Chunk.NEWLINE);

            // Add totals
            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(40);
            totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

            addTotalRow(totalsTable, "Subtotal:", String.format("$%.2f", subtotal));
            addTotalRow(totalsTable, "Tax (included):", String.format("$%.2f", facture.getTotalAmount() - subtotal));
            addTotalRow(totalsTable, "Total:", String.format("$%.2f", facture.getTotalAmount()));

            document.add(totalsTable);

            // Add footer
            document.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("Thank you for your business!", NORMAL_FONT);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Product", "Qty", "Price", "Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle, HEADER_FONT));
                    table.addCell(header);
                });
    }

    private void addInfoCell(PdfPTable table, String label, String value) {
        table.addCell(new PdfPCell(new Phrase(label, HEADER_FONT)));
        table.addCell(new PdfPCell(new Phrase(value, NORMAL_FONT)));
    }

    private void addTotalRow(PdfPTable table, String label, String value) {
        table.addCell(new PdfPCell(new Phrase(label, HEADER_FONT)));
        table.addCell(new PdfPCell(new Phrase(value, NORMAL_FONT)));
    }

    public Optional<Facture> getFactureById(Long id) throws Exception {
        return factureRepository.findById(id);
    }

    public List<Facture> getAllFactures() throws Exception {
        return factureRepository.findAll();
    }

    public List<Facture> getUserFactures(Long userId) throws Exception {
        return factureRepository.findFacturesByUserId(userId);
    }
}