package org.store.api.entity;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "command")
@XmlType(propOrder = {"id", "status", "date", "userId"})
public class Command {

    private Long id;
    private String status;
    private String date;
    private Long userId;

    // Getters with @XmlElement annotations
    @XmlElement
    public Long getId() { return id; }

    @XmlElement
    public String getStatus() { return status; }

    @XmlElement
    public String getDate() { return date; }

    @XmlElement
    public Long getUserId() { return userId; }
}