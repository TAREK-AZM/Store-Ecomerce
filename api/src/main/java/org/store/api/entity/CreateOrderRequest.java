package org.store.api.entity;

import java.util.List;

public class CreateOrderRequest {
    private Command command;
    private List<LineCommand> lineCommands;

    // Getters and setters
    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public List<LineCommand> getLineCommands() {
        return lineCommands;
    }

    public void setLineCommands(List<LineCommand> lineCommands) {
        this.lineCommands = lineCommands;
    }
}