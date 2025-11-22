package com.example_SE_Dental_Management.controller;

import com.example_SE_Dental_Management.entity.Appointment;
import com.example_SE_Dental_Management.entity.User;
import com.example_SE_Dental_Management.repository.AppointmentRepository;
import com.example_SE_Dental_Management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DentistApiController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dentists/me")
    public ResponseEntity<User> getCurrentDentist(@AuthenticationPrincipal UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/appointments/dentist")
    public List<Appointment> getDentistAppointments(@RequestParam String status, @AuthenticationPrincipal UserDetails userDetails) {
        String dentistFullName = userRepository.findByUsername(userDetails.getUsername())
                .map(User::getFullName)
                .orElseThrow(() -> new RuntimeException("Dentist not found"));

        // Get all appointments for this dentist by matching the start of dentistName
        // This will match "Dr. Nilusha Sudharaka (General Dentistry)" when dentistFullName is "Nilusha Sudharaka"
        return appointmentRepository.findByDentistNameContainingAndStatusOrderByAppointmentDateAsc(dentistFullName, status);
    }

    // THIS METHOD IS NOW CORRECTED TO PROPERLY HANDLE THE JSON PAYLOAD
    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<Appointment> updateAppointmentStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status"); // Correctly extracts the status string
        return appointmentRepository.findById(id)
                .map(appointment -> {
                    appointment.setStatus(newStatus);
                    Appointment updatedAppointment = appointmentRepository.save(appointment);
                    return ResponseEntity.ok(updatedAppointment);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}