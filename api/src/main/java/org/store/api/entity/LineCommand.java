package org.store.api.entity;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "lineCommand") // Root element for XML
public class LineCommand {
    private Long id;
    private int quantity;
    private Long productId; // Reference to the product
    private Long commandId; // Reference to the command

    // Getters with @XmlElement annotations
    @XmlElement
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @XmlElement
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @XmlElement
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    @XmlElement
    public Long getCommandId() { return commandId; }
    public void setCommandId(Long commandId) { this.commandId = commandId; }
}