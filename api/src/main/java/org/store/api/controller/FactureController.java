package org.store.api.controller;

import org.store.api.entity.Facture;
import org.store.api.service.FactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/factures")
@CrossOrigin(origins = "*")
public class FactureController {

    @Autowired
    private FactureService factureService;

    @GetMapping
    public ResponseEntity<List<Facture>> getAllFactures() {
        try {
            List<Facture> factures = factureService.getAllFactures();
            return ResponseEntity.ok(factures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Facture> getFacture(@PathVariable Long id) {
        try {
            return factureService.getFactureById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Facture>> getUserFactures(@PathVariable Long userId) {
        try {
            List<Facture> factures = factureService.getUserFactures(userId);
            return ResponseEntity.ok(factures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<InputStreamResource> getFacturePdf(@PathVariable Long id) {
        try {
            String pdfPath = "src/main/resources/factures/facture_" + id + ".pdf";
            File file = new File(pdfPath);

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facture_" + id + ".pdf");

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Error handler for generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + e.getMessage());
    }
}