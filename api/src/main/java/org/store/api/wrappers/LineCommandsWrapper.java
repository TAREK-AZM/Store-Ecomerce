package org.store.api.entity;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "lineCommands") // Root element for XML
public class LineCommandsWrapper {
    private List<LineCommand> lineCommands;

    @XmlElement(name = "lineCommand") // Each item in the list is a <lineCommand> element
    public List<LineCommand> getLineCommands() { return lineCommands; }
    public void setLineCommands(List<LineCommand> lineCommands) { this.lineCommands = lineCommands; }
}