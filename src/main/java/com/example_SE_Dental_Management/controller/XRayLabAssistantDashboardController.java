package com.example_SE_Dental_Management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class XRayLabAssistantDashboardController {

    /**
     * This method handles requests to the /xray/dashboard URL and returns
     * the name of the Thymeleaf template to be rendered, which is
     * 'xray-labassistant.html'.
     */
    @GetMapping("/xray/dashboard")
    public String showXrayLabAssistantDashboard() {
        return "xray-labassistant";
    }
}