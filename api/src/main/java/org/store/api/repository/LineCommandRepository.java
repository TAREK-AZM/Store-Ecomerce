package org.store.api.repository;

import org.store.api.entity.LineCommand;
import org.store.api.entity.LineCommandsWrapper;
import org.store.api.util.XmlUtil;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LineCommandRepository {

    private static final String LINE_COMMANDS_FILE = "src/main/resources/data/lineCommands.xml";

    // Read all line commands from the XML file
    public List<LineCommand> findAll() throws Exception {
        LineCommandsWrapper wrapper = XmlUtil.readXml(LINE_COMMANDS_FILE, LineCommandsWrapper.class);
        return wrapper.getLineCommands();
    }

    // Find a line command by ID
    public Optional<LineCommand> findById(Long id) throws Exception {
        return findAll().stream()
                .filter(lineCommand -> lineCommand.getId().equals(id))
                .findFirst();
    }

    // Save a line command (add or update)
    public void save(LineCommand lineCommand) throws Exception {
        List<LineCommand> lineCommands = findAll();
        lineCommands.removeIf(lc -> lc.getId().equals(lineCommand.getId())); // Remove existing line command if it exists
        lineCommands.add(lineCommand); // Add the new/updated line command
        LineCommandsWrapper wrapper = new LineCommandsWrapper();
        wrapper.setLineCommands(lineCommands);
        XmlUtil.writeXml(LINE_COMMANDS_FILE, wrapper);
    }

    // Delete a line command by ID
    public void deleteById(Long id) throws Exception {
        List<LineCommand> lineCommands = findAll();
        lineCommands.removeIf(lineCommand -> lineCommand.getId().equals(id)); // Remove the line command
        LineCommandsWrapper wrapper = new LineCommandsWrapper();
        wrapper.setLineCommands(lineCommands);
        XmlUtil.writeXml(LINE_COMMANDS_FILE, wrapper);
    }
}