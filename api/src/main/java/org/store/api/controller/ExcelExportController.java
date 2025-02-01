package org.store.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.store.api.service.ExcelExportService;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/export")
@CrossOrigin(origins = "*")
public class ExcelExportController {

    @Autowired
    private ExcelExportService excelExportService;

    @GetMapping("/users/excel")
    public ResponseEntity<?> exportUsersToExcel() {
        try {
            String filePath = excelExportService.exportUsersToExcel();

            // Create path from the generated file
            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());

            // Set the response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(path.getFileName().toString())
                    .build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error generating Excel file: " + e.getMessage());
        }
    }
}