package org.store.api.util;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReportGeneratorUtil {
    private static final Logger logger = LoggerFactory.getLogger(ReportGeneratorUtil.class);
    private static final String REPORTS_DIR = "src/main/resources/rapports/";
    private static final String XSLT_DIR = "src/main/resources/xslt/";

    public String generateMonthlyReport(String xmlFilePath, String reportType) throws ReportGenerationException {
        try {
            // Validate input files exist
            validateInputFiles(xmlFilePath, reportType);

            // Create directories if they don't exist
            createDirectories();

            // Generate unique filename for the report
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String outputFileName = String.format("%s_%s_report.html", reportType, timestamp);
            String outputPath = REPORTS_DIR + outputFileName;

            // Create transformer
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            String xsltPath = XSLT_DIR + reportType + ".xslt";

            logger.info("Loading XSLT from: {}", xsltPath);
            Source xslt = new StreamSource(new File(xsltPath));
            Transformer transformer = transformerFactory.newTransformer(xslt);

            // Set up input and output
            logger.info("Loading XML from: {}", xmlFilePath);
            Source xml = new StreamSource(new File(xmlFilePath));
            Result output = new StreamResult(new File(outputPath));

            // Transform the XML
            transformer.transform(xml, output);
            logger.info("Report generated successfully at: {}", outputPath);

            return outputPath;

        } catch (Exception e) {
            logger.error("Error generating report: ", e);
            throw new ReportGenerationException("Failed to generate report: " + e.getMessage(), e);
        }
    }

    private void validateInputFiles(String xmlFilePath, String reportType) throws ReportGenerationException {
        // Validate XML file
        File xmlFile = new File(xmlFilePath);
        if (!xmlFile.exists()) {
            throw new ReportGenerationException("XML file not found: " + xmlFilePath);
        }

        // Validate XSLT file
        File xsltFile = new File(XSLT_DIR + reportType + ".xslt");
        if (!xsltFile.exists()) {
            throw new ReportGenerationException("XSLT file not found: " + xsltFile.getPath());
        }
    }

    private void createDirectories() throws ReportGenerationException {
        try {
            // Create reports directory
            File reportsDir = new File(REPORTS_DIR);
            if (!reportsDir.exists() && !reportsDir.mkdirs()) {
                throw new ReportGenerationException("Failed to create reports directory: " + REPORTS_DIR);
            }

            // Create XSLT directory
            File xsltDir = new File(XSLT_DIR);
            if (!xsltDir.exists() && !xsltDir.mkdirs()) {
                throw new ReportGenerationException("Failed to create XSLT directory: " + XSLT_DIR);
            }
        } catch (Exception e) {
            throw new ReportGenerationException("Error creating directories: " + e.getMessage(), e);
        }
    }

    public byte[] getReportAsBytes(String reportPath) throws ReportGenerationException {
        try {
            File reportFile = new File(reportPath);
            if (!reportFile.exists()) {
                throw new ReportGenerationException("Report file not found: " + reportPath);
            }
            return java.nio.file.Files.readAllBytes(reportFile.toPath());
        } catch (IOException e) {
            throw new ReportGenerationException("Failed to read report file: " + e.getMessage(), e);
        }
    }
}

// Custom exception class for report generation errors
class ReportGenerationException extends Exception {
    public ReportGenerationException(String message) {
        super(message);
    }

    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}