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
import org.store.api.util.IdGenerator;
import org.store.api.entity.CreateOrderRequest;
import org.store.api.repository.UserRepository;
import org.store.api.entity.*;

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

    @Autowired
    private UserRepository userRepository;

    // Get all commands
    public List<Command> getAllCommands() throws Exception {
        return commandRepository.findAll();
    }

    // Get a command by ID
    public Optional<Command> getCommandById(Long id) throws Exception {
        return commandRepository.findById(id);
    }
    // createOrder

    public Long createOrder(CreateOrderRequest orderRequest) throws Exception {
        User user = orderRequest.getUser();
        List<LineCommand> lineCommands = orderRequest.getLineCommands();

        // 1. Check if the user exists by phone number
        Optional<User> existingUserOpt = userRepository.findByPhoneNumber(user.getPhoneNumber());

        User storedUser;
        if (existingUserOpt.isEmpty()) {
            // Assign a unique ID using the utility function
            user.setId(IdGenerator.generateUniqueId(userRepository.findAll(), User::getId));

            System.out.println("-----> this the user <------"+user);
            // Save the new user
            userRepository.save(user);
            storedUser = user; // Assign to the final variable
        } else {
            storedUser = existingUserOpt.get();
            System.out.println("---->the user exist<-----"+storedUser);

        }


        // 2. Create and save the command
        Command command = new Command();
        command.setUserId(storedUser.getId());
        command.setStatus("Pending");
        command.setDate(java.time.LocalDate.now().toString());

        // Assign a unique ID to the command
        command.setId(IdGenerator.generateUniqueId(commandRepository.findAll(), Command::getId));

        commandRepository.save(command);

        // 3. Associate line commands with the created command
        for (LineCommand line : lineCommands) {
            line.setCommandId(command.getId());

            // Assign a unique ID to each LineCommand
            line.setId(IdGenerator.generateUniqueId(lineCommandRepository.findAll(), LineCommand::getId));

            lineCommandRepository.save(line);
        }

        // 4. Generate the facture
        Facture facture = factureService.createFacture(command, lineCommands);

        return facture.getId(); // Return the facture ID
    }

    public void saveCommand(Command command) throws Exception {
        commandRepository.save(command);
    }

    // delete the command
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