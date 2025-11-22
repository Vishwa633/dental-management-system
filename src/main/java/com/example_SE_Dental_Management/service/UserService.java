package com.example_SE_Dental_Management.service;

import com.example_SE_Dental_Management.entity.User;
import com.example_SE_Dental_Management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * This is the core method for Spring Security.
     * It finds a user by their username for the login process.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        String userType = user.getType().toUpperCase();
        String role;

        // Only modify the X-RAY LAB ASSISTANT role
        if ("X-RAY LAB ASSISTANT".equals(userType)) {
            role = "ROLE_XRAY_ASSISTANT";
        } else {
            role = "ROLE_" + userType;
        }

        System.out.println("DEBUG: User: " + username + ", Type: " + userType + ", Role: " + role);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }

    // --- METHODS FOR PUBLIC PATIENT REGISTRATION ---

    /**
     * Handles new user sign-ups from the public registration form.
     * Automatically sets the user type to "Patient".
     */
    public void registerPatient(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already taken. Please choose another one.");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalStateException("An account with this email already exists.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setType("Patient");
        userRepository.save(user);
    }


    // --- METHODS FOR THE ADMIN DASHBOARD API ---

    /**
     * Finds all users, with an option to filter by user type.
     * Used by the admin dashboard.
     */
    public List<User> getAllUsers(String type) {
        if (type != null && !type.isEmpty() && !type.equals("All Users")) {
            return userRepository.findByType(type);
        }
        return userRepository.findAll();
    }

    /**
     * Finds a single user by their ID.
     * Used by the admin dashboard for editing.
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Saves a new user created by an admin.
     * This is more flexible than registerPatient as the type is set from the form.
     */
    public User saveUser(User user) {
        // Hashes the password only if it's a new, plain-text password
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Updates an existing user's details from the admin dashboard.
     */
    public User updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        existingUser.setFullName(userDetails.getFullName());
        existingUser.setDob(userDetails.getDob());
        existingUser.setGender(userDetails.getGender());
        existingUser.setContactNumber(userDetails.getContactNumber());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setUsername(userDetails.getUsername());
        existingUser.setType(userDetails.getType());

        // Only update password if a new one is provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    /**
     * Deletes a user by their ID, as requested from the admin dashboard.
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

