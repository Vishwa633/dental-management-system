package com.example_SE_Dental_Management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required.")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters.")
    @Column(name = "full_name")
    private String fullName;

    @NotBlank(message = "Contact number is required.")
    @Size(min = 10, max = 10, message = "Contact number must be between 10 and 10 digits.")
    @Column(name = "contact_number") // <-- THE FIX IS HERE
    private String contactNumber;

    @Past(message = "Date of birth must be in the past.")
    private LocalDate dob;

    @NotBlank(message = "Gender is required.")
    private String gender;

    @NotBlank(message = "Email is required.")
    @Email(message = "Please provide a valid email address.")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "user_type")
    private String type;

    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;
}