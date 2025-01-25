package org.store.api.repository;


import org.store.api.entity.Command;
import org.store.api.entity.CommandsWrapper;
import org.store.api.util.XmlUtil;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommandRepository {

    private static final String COMMANDS_FILE = "src/main/resources/data/commands.xml";

    // Read all commands from the XML file
    public List<Command> findAll() throws Exception {
        CommandsWrapper wrapper = XmlUtil.readXml(COMMANDS_FILE, CommandsWrapper.class);
        return wrapper.getCommands();
    }

    // Find a command by ID
    public Optional<Command> findById(Long id) throws Exception {
        return findAll().stream()
                .filter(command -> command.getId().equals(id))
                .findFirst();
    }

    // Save a command (add or update)
    public void save(Command command) throws Exception {
        List<Command> commands = findAll();
        commands.removeIf(c -> c.getId().equals(command.getId())); // Remove existing command if it exists
        commands.add(command); // Add the new/updated command
        CommandsWrapper wrapper = new CommandsWrapper();
        wrapper.setCommands(commands);
        XmlUtil.writeXml(COMMANDS_FILE, wrapper);
    }

    // Delete a command by ID
    public void deleteById(Long id) throws Exception {
        List<Command> commands = findAll();
        commands.removeIf(command -> command.getId().equals(id)); // Remove the command
        CommandsWrapper wrapper = new CommandsWrapper();
        wrapper.setCommands(commands);
        XmlUtil.writeXml(COMMANDS_FILE, wrapper);
    }
}

