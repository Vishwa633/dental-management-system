package com.example_SE_Dental_Management.controller;

import com.example_SE_Dental_Management.entity.User;
import com.example_SE_Dental_Management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DentistDashboardController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dentist/dashboard")
    public String showDentistDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Get the logged-in dentist's information
        User dentist = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Dentist not found"));

        // Add dentist info to the model so Thymeleaf can access it
        model.addAttribute("dentist", dentist);

        return "Dentist_dashboard";
    }
}