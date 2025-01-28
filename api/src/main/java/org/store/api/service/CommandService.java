package org.store.api.service;

import org.store.api.entity.Facture;
import org.store.api.entity.Product;
import org.store.api.entity.LineCommand;
import org.store.api.entity.Command;
import org.store.api.repository.CommandRepository;
import org.store.api.repository.ProductRepository;
import org.store.api.repository.LineCommandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File; // Add this import
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Data;

@Data
@Service
public class CommandService {

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private LineCommandRepository lineCommandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FactureService factureService;


    // Get all commands
    public List<Command> getAllCommands() throws Exception {
        return commandRepository.findAll();
    }

    // Get a command by ID
    public Optional<Command> getCommandById(Long id) throws Exception {
        return commandRepository.findById(id);
    }

    public Long createOrder(Command command, List<LineCommand> lineCommands) throws Exception {
        // Save command
        commandRepository.save(command);


        // Update line commands with command ID
        for (LineCommand line : lineCommands) {
            line.setCommandId(command.getId());
            lineCommandRepository.save(line);
        }

        // Generate facture
        return factureService.createFacture(command, lineCommands).getId();

    }

    // Delete a command by ID
    public void deleteCommand(Long id) throws Exception {
        commandRepository.deleteById(id);
    }

    // Get products for a specific command
    public List<Product> getProductsByCommandId(Long commandId) throws Exception {
        // Step 1: Find all line commands for the given command ID
        List<LineCommand> lineCommands = lineCommandRepository.findLineCommandsByCommandId(commandId);
        System.out.println("test lines of commands" + lineCommands);
        // Step 2: Extract product IDs from the line commands
        List<Long> productIds = lineCommands.stream()
                .map(LineCommand::getProductId)
                .collect(Collectors.toList());
        // testgitting the products
        System.out.println("those are the product of comand" + productIds);

        // Step 3: Fetch products using the product IDs
        return productRepository.findAll().stream()
                .filter(product -> productIds.contains(product.getId()))
                .collect(Collectors.toList());
    }

    // Get all commands for a specific user
    public List<Command> getUserCommands(long userId) throws Exception {
        return commandRepository.getUserCommands(userId);
    }
}