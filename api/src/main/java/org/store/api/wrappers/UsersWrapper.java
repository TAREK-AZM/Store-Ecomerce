package org.store.api.entity;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "users") // Root element for XML
public class UsersWrapper {
    private List<User> users;

    @XmlElement(name = "user") // Each item in the list is a <user> element
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
}