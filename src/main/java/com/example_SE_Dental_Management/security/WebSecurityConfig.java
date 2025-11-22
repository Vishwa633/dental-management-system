package com.example_SE_Dental_Management.security;

import com.example_SE_Dental_Management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for ALL API endpoints (not just X-ray)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/api/**",           // All API endpoints
                                "/api/xray-requests/*" // Keep existing X-ray endpoints
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        // --- THIS IS THE CORRECT ORDER OF RULES ---
                        // 1. Publicly accessible paths
                        .requestMatchers("/", "/index.html", "/login", "/register", "/css/**", "/image/**", "/js/**", "/xray-images/**").permitAll()

                        // 2. Patient paths - UPDATED
                        .requestMatchers("/profile/**", "/appointments/book", "/appointments/cancel/**",
                                "/users/profile", "/users/edit/**", "/users/update/**").hasRole("PATIENT")

                        // DENTIST-only paths (API endpoints moved to shared section below)
                        .requestMatchers("/dentist/dashboard", "/api/dentists/me").hasRole("DENTIST")

                        // RECEPTIONIST paths - UPDATED: Only dashboard path, API moved to shared
                        .requestMatchers("/receptionist/dashboard").hasRole("RECEPTIONIST")


                        // SUPPLIER paths
                        .requestMatchers("/supplier/dashboard", "/api/supplier/**").hasRole("SUPPLIER")

                        // SHARED API ENDPOINTS - BOTH DENTIST AND RECEPTIONIST
                        .requestMatchers("/api/appointments/**").hasAnyRole("DENTIST", "RECEPTIONIST")
                        .requestMatchers("/api/alerts/**").hasAnyRole("DENTIST", "RECEPTIONIST")

                        // X-RAY API ENDPOINTS - allow both DENTIST and XRAY_ASSISTANT
                        .requestMatchers("/api/xray-requests/dentist").hasRole("DENTIST") // Only dentists can see their own requests
                        .requestMatchers("/api/xray-requests/pending", "/api/xray-requests/processed",
                                "/api/xray-requests/emergency", "/api/xray-requests/**").hasAnyRole("DENTIST", "XRAY_ASSISTANT")

                        // XRAY_ASSISTANT-only paths
                        .requestMatchers("/xray/dashboard").hasRole("XRAY_ASSISTANT")

                        // 3. General Admin path (this must come AFTER the specific ones)
                        .requestMatchers("/admin/**", "/api/users/**").hasRole("ADMIN")

                        // 4. Any other request must be authenticated
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/index.html?logout=true")
                        .permitAll()
                );

        return http.build();
    }
}