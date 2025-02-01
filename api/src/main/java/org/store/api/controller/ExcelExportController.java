package org.store.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
// my services
import org.store.api.service.ExcelExportService;
import org.store.api.service.UserService;
import org.store.api.service.ProductService;
import org.store.api.service.CommandService;
import org.store.api.service.CategoryService;



import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/export")
@CrossOrigin(origins = "*")
public class ExcelExportController {

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;
    // Autowire other services as needed
    @Autowired
    private CommandService commandService;

    @Autowired
    private  CategoryService  categoryService;

    @GetMapping("/{entityType}/excel")
    public ResponseEntity<?> exportToExcel(@PathVariable String entityType) {
        try {
            String filePath;
            switch (entityType.toLowerCase()) {
                case "users":
                    filePath = excelExportService.exportToExcel(
                            userService.getAllUsers(), "Users");
                    break;
                case "products":
                    filePath = excelExportService.exportToExcel(
                            productService.getAllProducts(), "Products");
                    break;
                case "commands":
                    filePath = excelExportService.exportToExcel(
                            commandService.getAllCommands(), "Commands");
                    break;
                case "categories":
                    filePath = excelExportService.exportToExcel(
                            categoryService.getAllCategories(), "Categories");
                    break;
                // Add other entity types here
                default:
                    return ResponseEntity.badRequest()
                            .body("Unsupported entity type: " + entityType);
            }

            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());

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