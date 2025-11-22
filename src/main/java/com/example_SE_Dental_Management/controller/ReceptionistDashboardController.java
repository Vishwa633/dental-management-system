package com.example_SE_Dental_Management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReceptionistDashboardController {

    @GetMapping("/receptionist/dashboard")
    public String showReceptionistDashboard() {
        return "receptionist"; // This returns your receptionist.html file
    }
}