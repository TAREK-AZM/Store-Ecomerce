package org.store.api.entity;

import java.util.List;

public class CreateOrderRequest {
    private User user;
    private List<LineCommand> lineCommands;

    // Getters and setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<LineCommand> getLineCommands() {
        return lineCommands;
    }

    public void setLineCommands(List<LineCommand> lineCommands) {
        this.lineCommands = lineCommands;
    }
}