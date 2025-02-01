package org.store.api.repository;

import org.store.api.entity.LineCommand;
import org.store.api.entity.LineCommandsWrapper;
import org.store.api.util.XmlUtil;
import org.springframework.stereotype.Repository;
import org.store.api.util.IdGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class LineCommandRepository {

    private static final String LINE_COMMANDS_FILE = "src/main/resources/data/lineCommands.xml";
    private static final String LINE_COMMANDS_XSD = "src/main/resources/data/lineCommands.xsd"; // Path to XSD

    // Read all line commands from the XML file with XSD validation
    public List<LineCommand> findAll() throws Exception {
        File file = new File(LINE_COMMANDS_FILE);
        if (!file.exists() || file.length() == 0) {
            // Return an empty list if the file is empty or doesn't exist
            return new ArrayList<>();
        }
        LineCommandsWrapper wrapper = XmlUtil.readXml(LINE_COMMANDS_FILE, LineCommandsWrapper.class, LINE_COMMANDS_XSD);
        return wrapper.getLineCommands();
    }

    // Find a line command by ID
    public Optional<LineCommand> findById(Long id) throws Exception {
        List<LineCommand> lineCommands = findAll();
        return lineCommands.stream()
                .filter(lineCommand -> lineCommand.getId().equals(id))
                .findFirst();
    }

    // Save a line command (add or update) with XSD validation
    public void save(LineCommand lineCommand) throws Exception {
        List<LineCommand> lineCommands = findAll();

        // Check if the line command already exists
        Optional<LineCommand> existingLineCommand = lineCommands.stream()
                .filter(lc -> lc.getId().equals(lineCommand.getId()))
                .findFirst();
        // If it exists, remove it
        existingLineCommand.ifPresent(lineCommands::remove);
        if(!(lineCommand.getId() !=null)){// if the product yet created
            lineCommand.setId(IdGenerator.generateUniqueId(findAll(), LineCommand::getId));
        }
        // Add the new/updated line command
        lineCommands.add(lineCommand);

        // Wrap the list and write to XML with XSD validation
        LineCommandsWrapper wrapper = new LineCommandsWrapper();
        wrapper.setLineCommands(lineCommands);
        XmlUtil.writeXml(LINE_COMMANDS_FILE, wrapper, LINE_COMMANDS_XSD);
    }

    // Delete a line command by ID
    public void deleteById(Long id) throws Exception {
        List<LineCommand> lineCommands = findAll();

        // Remove the line command if it exists
        boolean removed = lineCommands.removeIf(lineCommand -> lineCommand.getId().equals(id));

        if (removed) {
            // Wrap the updated list and write to XML with XSD validation
            LineCommandsWrapper wrapper = new LineCommandsWrapper();
            wrapper.setLineCommands(lineCommands);
            XmlUtil.writeXml(LINE_COMMANDS_FILE, wrapper, LINE_COMMANDS_XSD);
        }
    }


    // Find line commands by command ID
    public List<LineCommand> findLineCommandsByCommandId(Long commandId) throws Exception {
        return findAll().stream()
                .filter(lineCommand -> lineCommand.getCommandId().equals(commandId))
                .toList();
    }


}