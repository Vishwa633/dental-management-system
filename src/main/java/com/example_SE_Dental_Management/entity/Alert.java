package com.example_SE_Dental_Management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dentist_name", length = 255)
    private String dentistName;

    @Column(name = "patient_names", length = 1000)
    private String patientNames;

    @Column(name = "patient_user_ids", length = 1000) // NEW FIELD
    private String patientUserIds;

    @Column(name = "message", length = 2000)
    private String message;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    // Constructors, getters, setters...
    public Alert() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }

    public String getPatientNames() { return patientNames; }
    public void setPatientNames(String patientNames) { this.patientNames = patientNames; }

    public String getPatientUserIds() { return patientUserIds; } // NEW
    public void setPatientUserIds(String patientUserIds) { this.patientUserIds = patientUserIds; } // NEW

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getSentDate() { return sentDate; }
    public void setSentDate(LocalDateTime sentDate) { this.sentDate = sentDate; }
}