package com.example_SE_Dental_Management.security;

import com.example_SE_Dental_Management.security.strategies.RedirectStrategyFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    @Autowired
    private RedirectStrategyFactory strategyFactory;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        System.out.println("=== AUTHENTICATION SUCCESS ===");
        System.out.println("User: " + authentication.getName());
        System.out.println("All Authorities: " + authentication.getAuthorities());

        // Iterate through ALL authorities to find a matching strategy
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            System.out.println("Checking role: " + role);

            var strategy = strategyFactory.getStrategy(role);
            if (strategy.isPresent()) {
                System.out.println("Found strategy for role: " + role + ", redirecting...");
                strategy.get().redirect(response);
                return; // IMPORTANT: Return after successful redirect
            }
        }

        // If no specific strategy found, use default redirect
        System.out.println("No specific strategy found, redirecting to default");
        response.sendRedirect("/");
    }
}