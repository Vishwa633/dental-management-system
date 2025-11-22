package com.example_SE_Dental_Management.security.strategies;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component("ROLE_ADMIN") // The name matches the user's role authority
public class AdminRedirectStrategy implements RedirectStrategy {
    @Override
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/admin");
    }
}