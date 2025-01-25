package org.store.api.service;

import org.store.api.entity.User;
import org.store.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Get all users
    public List<User> getAllUsers() throws Exception {
        return userRepository.findAll();
    }

    // Get a user by ID
    public Optional<User> getUserById(Long id) throws Exception {
        return userRepository.findById(id);
    }

    // Create or update a user
    public void saveUser(User user) throws Exception {
        userRepository.save(user);
    }

    // Delete a user by ID
    public void deleteUser(Long id) throws Exception {
        userRepository.deleteById(id);
    }
}