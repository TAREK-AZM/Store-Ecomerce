package org.store.api.repository;

import org.store.api.entity.User;
import org.store.api.entity.UsersWrapper;
import org.store.api.util.XmlUtil;
import org.springframework.stereotype.Repository;
import org.store.api.util.IdGenerator;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private static final String USERS_FILE = "src/main/resources/data/users.xml";
    private static final String USERS_XSD = "src/main/resources/data/users.xsd"; // Path to XSD

    // Read all users from the XML file
    public List<User> findAll() throws Exception {
        UsersWrapper wrapper = XmlUtil.readXml(USERS_FILE, UsersWrapper.class,USERS_XSD);
        return wrapper.getUsers();
    }

    // Find a user by ID
    public Optional<User> findById(Long id) throws Exception {
        return findAll().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) throws Exception {
        return findAll().stream()
                .filter(user -> user.getPhoneNumber().equals(phoneNumber))
                .findFirst();
    }

    // Find a check user by phone number
    public Boolean checkUserExistByPhoneNumber(String phoneNumber) throws Exception {
        return findAll().stream()
                .anyMatch(user -> user.getPhoneNumber().equals(phoneNumber));
    }

    // Save a user (add or update)
    public void save(User user) throws Exception {
        List<User> users = findAll();
        users.removeIf(u -> u.getId().equals(user.getId())); // Remove existing user if it exists
        if(!(user.getId() !=null)){// if the product yet created
            user.setId(IdGenerator.generateUniqueId(findAll(), User::getId));
        }
        users.add(user); // Add the new/updated user
        UsersWrapper wrapper = new UsersWrapper();
        wrapper.setUsers(users);
        XmlUtil.writeXml(USERS_FILE, wrapper,USERS_XSD);
    }

    // Delete a user by ID
    public void deleteById(Long id) throws Exception {
        List<User> users = findAll();
        users.removeIf(user -> user.getId().equals(id)); // Remove the user
        UsersWrapper wrapper = new UsersWrapper();
        wrapper.setUsers(users);
        XmlUtil.writeXml(USERS_FILE, wrapper,USERS_XSD);
    }

    //
}


