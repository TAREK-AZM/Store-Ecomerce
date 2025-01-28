package org.store.api.entity;

import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;

@Data
@XmlRootElement(name = "facture") // Root element for XML
@XmlType(propOrder = {"id", "commandId", "userId", "invoiceDate","totalAmount","status"})

public class Facture {


    private Long id;


    private Long commandId;


    private Long userId;

    private String invoiceDate;

    private Double totalAmount;

    private String status;

    // Explicit getters and setters (Lombok will generate these, but you can define them explicitly if needed)

    @XmlElement // Maps to <id> in XML
    public Long getId() {
        return id;
    }

    @XmlElement // Maps to <commandId> in XML
    public Long getCommandId() {
        return commandId;
    }

    @XmlElement // Maps to <userId> in XML
    public Long getUserId() {
        return userId;
    }

    @XmlElement // Maps to <invoiceDate> in XML
    public String getInvoiceDate() {
        return invoiceDate;
    }

    @XmlElement // Maps to <totalAmount> in XML
    public Double getTotalAmount() {
        return totalAmount;
    }

    @XmlElement // Maps to <status> in XML
    public String getStatus() {
        return status;
    }


}