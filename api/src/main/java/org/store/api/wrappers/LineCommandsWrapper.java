package org.store.api.entity;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "lineCommands")
public class LineCommandsWrapper {

    private List<LineCommand> lineCommands;

    @XmlElement(name = "lineCommand")
    public List<LineCommand> getLineCommands() {
        return lineCommands;
    }

    public void setLineCommands(List<LineCommand> lineCommands) {
        this.lineCommands = lineCommands;
    }
}