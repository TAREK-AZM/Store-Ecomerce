package org.store.api.controller;

import org.store.api.entity.CreateOrderRequest;
import org.store.api.entity.Product;
import org.store.api.entity.LineCommand;
import org.store.api.entity.Command;
import org.store.api.service.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/commands")
@CrossOrigin(origins = "*")
public class CommandController {

    @Autowired
    private CommandService commandService;

    // Get all commands
    @GetMapping
    public List<Command> getAllCommands() throws Exception {
        return commandService.getAllCommands();
    }

    // Get a command by ID
    @GetMapping("/{id}")
    public Optional<Command> getCommandById(@PathVariable Long id) throws Exception {
        return commandService.getCommandById(id);
    }

    // Create or update a command
//    @PostMapping
//    public void saveCommand(@RequestBody Command command) throws Exception {
//        commandService.saveCommand(command);
//    }

//    public void createOrder(@RequestBody CreateOrderRequest request) throws Exception {
//        commandService.createOrder(request.getCommand(), request.getLineCommands());
//    }
    @PostMapping("/create")
    public ResponseEntity<InputStreamResource> createOrder(@RequestBody CreateOrderRequest orderRequest) {
        try {
            Long FactureID = commandService.createOrder(orderRequest.getCommand(), orderRequest.getLineCommands());
            return generatePdfResponse(FactureID);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete a command by ID
    @DeleteMapping("/{id}")
    public void deleteCommand(@PathVariable Long id) throws Exception {
        commandService.deleteCommand(id);
    }

    // Get products for a specific command
    @GetMapping("/{commandId}/products")
    public List<Product> getProductsByCommandId(@PathVariable Long commandId) throws Exception {
        return commandService.getProductsByCommandId(commandId);
    }

    // Get all commands for a specific user
    @GetMapping("/userCommands/{userId}")
    public List<Command> getUserCommands(@PathVariable long userId) throws Exception {
        return commandService.getUserCommands(userId);
    }



    // generate the commad Facture
    private ResponseEntity<InputStreamResource> generatePdfResponse(Long commandId) {
        try {
            String pdfPath = "src/main/resources/factures/facture_" + commandId + ".pdf";
            File file = new File(pdfPath);

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facture_" + commandId + ".pdf");

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

}