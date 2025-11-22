// EmergencyRequest.java
package com.example_SE_Dental_Management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_requests")
@Data
public class EmergencyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patientName;

    @Column(nullable = false)
    private String xrayType;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime requestDate;

    private String imagePath;

    @Column(length = 500)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        requestDate = LocalDateTime.now();
    }
}