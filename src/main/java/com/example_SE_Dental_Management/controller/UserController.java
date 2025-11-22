package com.example_SE_Dental_Management.controller;

import com.example_SE_Dental_Management.entity.Alert;
import com.example_SE_Dental_Management.entity.User;
import com.example_SE_Dental_Management.repository.AlertRepository;
import com.example_SE_Dental_Management.repository.UserRepository;
import com.example_SE_Dental_Management.service.UserService;
import jakarta.validation.Valid; // Import for validation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Import for validation results
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlertRepository alertRepository; // NEW: Added for user alerts


    /**
     * Shows the main homepage (index.html).
     */
    @GetMapping({"/", "/index.html"})
    public String showIndexPage() {
        return "index";
    }

    /**
     * Shows the login page (login.html).
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    /**
     * Shows the user registration page (register.html).
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    /**
     * Processes the new user registration form submission with validation.
     */
    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        // Check for validation errors defined in the User entity
        if (bindingResult.hasErrors()) {
            // If errors exist, return to the registration form.
            // Thymeleaf will automatically display the error messages.
            return "register";
        }

        try {
            userService.registerPatient(user);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (IllegalStateException e) {
            // This handles custom errors like "username already exists"
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    /**
     * Shows the logged-in patient's profile page (profile-user.html).
     */
    @GetMapping("/profile")
    public String showProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            System.out.println("DEBUG: Loading profile for user: " + userDetails.getUsername());

            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            System.out.println("DEBUG: Found user: " + user.getFullName() + " (ID: " + user.getId() + ")");

            // Manual filtering approach (from our discussion)
            List<Alert> allAlerts = alertRepository.findAllByOrderBySentDateDesc();
            List<Alert> userAlerts = new ArrayList<>();

            System.out.println("DEBUG: Total alerts in system: " + allAlerts.size());

            for (Alert alert : allAlerts) {
                System.out.println("DEBUG: Checking alert ID " + alert.getId() +
                        " - PatientUserIds: '" + alert.getPatientUserIds() + "'");

                // Handle NULL and empty patientUserIds
                if (alert.getPatientUserIds() != null && !alert.getPatientUserIds().trim().isEmpty()) {
                    String[] userIds = alert.getPatientUserIds().split(",");
                    for (String userIdStr : userIds) {
                        if (userIdStr.trim().equals(user.getId().toString())) {
                            userAlerts.add(alert);
                            System.out.println("DEBUG: ✅ Found matching alert for user: " + alert.getMessage());
                            break;
                        }
                    }
                }
            }

            System.out.println("DEBUG: User has " + userAlerts.size() + " alerts");

            model.addAttribute("user", user);
            model.addAttribute("appointments", user.getAppointments());
            model.addAttribute("userAlerts", userAlerts);

            return "profile-user";

        } catch (Exception e) {
            System.out.println("ERROR in profile: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            return "profile-user";
        }
    }

    /**
     * Shows the logged-in patient's profile page via /users/profile URL
     * This is needed for the edit profile page buttons
     */
    @GetMapping("/users/profile")
    public String showProfileViaUsersPath(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Reuse the same logic as your existing /profile endpoint
        try {
            System.out.println("DEBUG: Loading profile for user: " + userDetails.getUsername());

            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            System.out.println("DEBUG: Found user: " + user.getFullName() + " (ID: " + user.getId() + ")");

            // Manual filtering approach (from our discussion)
            List<Alert> allAlerts = alertRepository.findAllByOrderBySentDateDesc();
            List<Alert> userAlerts = new ArrayList<>();

            System.out.println("DEBUG: Total alerts in system: " + allAlerts.size());

            for (Alert alert : allAlerts) {
                System.out.println("DEBUG: Checking alert ID " + alert.getId() +
                        " - PatientUserIds: '" + alert.getPatientUserIds() + "'");

                // Handle NULL and empty patientUserIds
                if (alert.getPatientUserIds() != null && !alert.getPatientUserIds().trim().isEmpty()) {
                    String[] userIds = alert.getPatientUserIds().split(",");
                    for (String userIdStr : userIds) {
                        if (userIdStr.trim().equals(user.getId().toString())) {
                            userAlerts.add(alert);
                            System.out.println("DEBUG: ✅ Found matching alert for user: " + alert.getMessage());
                            break;
                        }
                    }
                }
            }

            System.out.println("DEBUG: User has " + userAlerts.size() + " alerts");

            model.addAttribute("user", user);
            model.addAttribute("appointments", user.getAppointments());
            model.addAttribute("userAlerts", userAlerts);

            return "profile-user";

        } catch (Exception e) {
            System.out.println("ERROR in profile: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            return "profile-user";
        }
    }
    /**
     * Shows the form for editing a user's profile (edit-profile-patient.html).
     */
    @GetMapping("/users/edit/{id}")
    public String showEditProfileForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        model.addAttribute("user", user);
        return "edit-profile-patient";
    }

    /**
     * Processes the profile update form submission.
     */
    @PostMapping("/users/update/{id}")
    public String updateProfile(@PathVariable Long id, @ModelAttribute("user") User userDetails, RedirectAttributes redirectAttributes) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setFullName(userDetails.getFullName());
        existingUser.setDob(userDetails.getDob());
        existingUser.setGender(userDetails.getGender());
        existingUser.setContactNumber(userDetails.getContactNumber());
        existingUser.setEmail(userDetails.getEmail());
        userRepository.save(existingUser);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/profile";
    }
    

    }

