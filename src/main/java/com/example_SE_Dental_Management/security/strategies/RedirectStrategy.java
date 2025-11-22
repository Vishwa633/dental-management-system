package com.example_SE_Dental_Management.security.strategies;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface RedirectStrategy {
    void redirect(HttpServletResponse response) throws IOException;
}