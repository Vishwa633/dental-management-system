package com.example_SE_Dental_Management.security.strategies;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component("ROLE_RECEPTIONIST") // The name MUST match the role from Spring Security
public class ReceptionistRedirectStrategy implements RedirectStrategy {
    @Override
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/receptionist/dashboard");
    }
}