package com.example_SE_Dental_Management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "appointments")
@Data
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientName;
    private int patientAge;
    private String patientContact; // Changed for consistency
    private String dentistName;
    private String specialization;

    private String gender;

    private LocalDate appointmentDate;
    private String preferredTime;
    private String status = "PENDING";
    // --- THIS IS THE NEW FIELD ---
    @Column(name = "appointment_notes", length = 500) // Added a length limit
    private String appointmentNotes;
    // ----------------------------
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User patient;
}