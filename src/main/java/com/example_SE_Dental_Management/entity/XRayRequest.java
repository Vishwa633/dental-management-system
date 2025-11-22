package com.example_SE_Dental_Management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "xray_requests")
@Data
public class XRayRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patientName;

    @Column(length = 500, nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate requestDate;

    // The status will be 'PENDING' by default for all new requests.
    @Column(nullable = false)
    private String status = "PENDING";

    // This field will be null initially. The X-ray Lab Assistant will update it later.
    private String imagePath;


    // This creates a link to the user (dentist) who made the request.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentist_id", nullable = false)
    private User dentist;

    // ADD THIS FIELD FOR UPLOAD NOTES:
    @Column(length = 1000)
    private String notes;
}