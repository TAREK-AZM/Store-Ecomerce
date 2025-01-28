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

@RestController
@RequestMapping("/api/commands")
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

    @PostMapping("/create")
    public void createOrder(@RequestBody CreateOrderRequest request) throws Exception {
        commandService.createOrder(request.getCommand(), request.getLineCommands());
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
}