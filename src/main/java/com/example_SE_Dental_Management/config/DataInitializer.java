package com.example_SE_Dental_Management.config;

import com.example_SE_Dental_Management.entity.User;
import com.example_SE_Dental_Management.repository.UserRepository;
import com.example_SE_Dental_Management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    // Use the service so passwords are hashed consistently
    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        try {
            if (userRepository.count() == 0) {
                // Create a default admin user with VALID fields
                User adminUser = new User();

                // Use the exact field names expected by your User entity
                adminUser.setFullName("Default Admin");
                adminUser.setUsername("admin");

                // Plain password here; the service will hash it before saving
                adminUser.setPassword("password");

                adminUser.setType("Admin");
                adminUser.setEmail("admin@clinic.com");
                adminUser.setGender("Other");

                // IMPORTANT: contactNumber must satisfy @Size(10,10) in your entity.
                // Provide only digits (10 chars). If you want formatted numbers, strip them before save.
                adminUser.setContactNumber("0000000000");

                adminUser.setDob(LocalDate.parse("2000-01-01"));

                // Save via the UserService so the password gets encoded
                userService.saveUser(adminUser);

                System.out.println(">>> Created default admin user 'admin' (password: password)");
            } else {
                // For debugging: print whether an 'admin' user exists
                Optional<User> admin = userRepository.findByUsername("admin");
                admin.ifPresent(u -> System.out.println(">>> Admin user already exists: " + u.getUsername()));
            }
        } catch (Exception ex) {
            // If validation fails or DB errors occur, log them but don't crash silently.
            System.err.println("DataInitializer error: " + ex.getClass().getName() + " - " + ex.getMessage());
            ex.printStackTrace();
            // Rethrow if you want startup to fail too; currently we log so developer can see the cause.
            // throw ex;
        }
    }
}
