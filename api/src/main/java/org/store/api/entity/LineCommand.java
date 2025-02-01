package org.store.api.entity;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

//@XmlRootElement(name = "lineCommand")
@XmlType(propOrder = {"id", "quantity", "productId", "commandId"})
public class LineCommand {

    private Long id;
    private int quantity;
    private Long productId;
    private Long commandId;

    // Default constructor required for JAXB
    public LineCommand() {
    }

    @XmlElement(required = true)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlElement(required = true)
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @XmlElement(required = true)
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @XmlElement(required = true)
    public Long getCommandId() {
        return commandId;
    }

    public void setCommandId(Long commandId) {
        this.commandId = commandId;
    }

    @Override
    public String toString() {
        return "LineCommand{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", productId=" + productId +
                ", commandId=" + commandId +
                '}';
    }
}