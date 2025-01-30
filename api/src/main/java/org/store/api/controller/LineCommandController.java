package org.store.api.controller;

import org.store.api.entity.LineCommand;
import org.store.api.service.LineCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/line-commands")
@CrossOrigin(origins = "*")
public class LineCommandController {

    @Autowired
    private LineCommandService lineCommandService;

    // Get all line commands
    @GetMapping
    public List<LineCommand> getAllLineCommands() throws Exception {
        return lineCommandService.getAllLineCommands();
    }

    // Get a line command by ID
    @GetMapping("/{id}")
    public Optional<LineCommand> getLineCommandById(@PathVariable Long id) throws Exception {
        return lineCommandService.getLineCommandById(id);
    }

    // Create or update a line command
    @PostMapping
    public void saveLineCommand(@RequestBody LineCommand lineCommand) throws Exception {
        lineCommandService.saveLineCommand(lineCommand);
    }

    // Delete a line command by ID
    @DeleteMapping("/{id}")
    public void deleteLineCommand(@PathVariable Long id) throws Exception {
        lineCommandService.deleteLineCommand(id);
    }
}