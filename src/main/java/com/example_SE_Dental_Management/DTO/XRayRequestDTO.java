package com.example_SE_Dental_Management.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class XRayRequestDTO {
    private Long id;
    private String patientName;
    private String description;
    private LocalDate requestDate;
    private String status;
    private String imagePath;
    private String dentistName; // Only include what you need

    public XRayRequestDTO(Long id, String patientName, String description,
                          LocalDate requestDate, String status, String imagePath, String dentistName) {
        this.id = id;
        this.patientName = patientName;
        this.description = description;
        this.requestDate = requestDate;
        this.status = status;
        this.imagePath = imagePath;
        this.dentistName = dentistName;
    }
}