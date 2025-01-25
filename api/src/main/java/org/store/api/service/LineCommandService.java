package org.store.api.service;

import org.store.api.entity.LineCommand;
import org.store.api.repository.LineCommandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LineCommandService {

    @Autowired
    private LineCommandRepository lineCommandRepository;

    // Get all line commands
    public List<LineCommand> getAllLineCommands() throws Exception {
        return lineCommandRepository.findAll();
    }

    // Get a line command by ID
    public Optional<LineCommand> getLineCommandById(Long id) throws Exception {
        return lineCommandRepository.findById(id);
    }

    // Create or update a line command
    public void saveLineCommand(LineCommand lineCommand) throws Exception {
        lineCommandRepository.save(lineCommand);
    }

    // Delete a line command by ID
    public void deleteLineCommand(Long id) throws Exception {
        lineCommandRepository.deleteById(id);
    }
}