package org.store.api.service;

import org.store.api.entity.Command;
import org.store.api.repository.CommandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommandService {

    @Autowired
    private CommandRepository commandRepository;

    // Get all commands
    public List<Command> getAllCommands() throws Exception {
        return commandRepository.findAll();
    }

    // Get a command by ID
    public Optional<Command> getCommandById(Long id) throws Exception {
        return commandRepository.findById(id);
    }

    // Create or update a command
    public void saveCommand(Command command) throws Exception {
        commandRepository.save(command);
    }

    // Delete a command by ID
    public void deleteCommand(Long id) throws Exception {
        commandRepository.deleteById(id);
    }
}