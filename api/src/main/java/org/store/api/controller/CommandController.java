package org.store.api.controller;

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
    @PostMapping
    public void saveCommand(@RequestBody Command command) throws Exception {
        commandService.saveCommand(command);
    }

    // Delete a command by ID
    @DeleteMapping("/{id}")
    public void deleteCommand(@PathVariable Long id) throws Exception {
        commandService.deleteCommand(id);
    }
}