package org.store.api.entity;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "commands") // Root element for XML
public class CommandsWrapper {
    private List<Command> commands;

    @XmlElement(name = "command") // Each item in the list is a <command> element
    public List<Command> getCommands() { return commands; }
    public void setCommands(List<Command> commands) { this.commands = commands; }
}