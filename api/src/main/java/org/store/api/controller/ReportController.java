package org.store.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.store.api.util.ReportGeneratorUtil;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private static final String PRODUCTS_FILE = "src/main/resources/data/products.xml";

    @Autowired
    private ReportGeneratorUtil reportGeneratorUtil;

    @GetMapping("/monthly/products")
    public ResponseEntity<?> generateMonthlyProductReport() {
        logger.info("Starting monthly product report generation");

        try {
            // Generate the report
            String reportPath = reportGeneratorUtil.generateMonthlyReport(PRODUCTS_FILE, "monthly-product");
            logger.info("Report generated at: {}", reportPath);

            byte[] reportContent = reportGeneratorUtil.getReportAsBytes(reportPath);
            ByteArrayResource resource = new ByteArrayResource(reportContent);

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monthly_product_report.html");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(reportContent.length)
                    .contentType(MediaType.TEXT_HTML)
                    .body(resource);

        } catch (Exception e) {
            logger.error("Error generating report: ", e);
            return ResponseEntity
                    .internalServerError()
                    .body("Error generating report: " + e.getMessage());
        }
    }
}