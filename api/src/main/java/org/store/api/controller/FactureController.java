package org.store.api.controller;

import org.store.api.entity.Facture;
import org.store.api.service.FactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/factures")
public class FactureController {

    @Autowired
    private FactureService factureService;

    @GetMapping
    public List<Facture> getAllFactures() {
        return factureService.getAllFactures();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Facture> getFacture(@PathVariable Long id) {
        return factureService.getFactureById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<Facture> getUserFactures(@PathVariable Long userId) {
        return factureService.getUserFactures(userId);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<InputStreamResource> getFacturePdf(@PathVariable Long id) throws Exception {
        String pdfPath = "src/main/resources/factures/facture_" + id + ".pdf";
        File file = new File(pdfPath);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facture_" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(new FileInputStream(file)));
    }
}