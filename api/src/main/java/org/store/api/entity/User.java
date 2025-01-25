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
@XmlRootElement(name = "user") // Root element for XML
public class User {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;

    // Getters with @XmlElement annotations
    @XmlElement
    public Long getId() { return id; }

    @XmlElement
    public String getUsername() { return username; }

    @XmlElement
    public String getEmail() { return email; }

    @XmlElement
    public String getFirstName() { return firstName; }

    @XmlElement
    public String getLastName() { return lastName; }

    @XmlElement
    public String getPhoneNumber() { return phoneNumber; }

    @XmlElement
    public String getAddress() { return address; }
}