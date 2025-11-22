package com.example_SE_Dental_Management.controller;

import com.example_SE_Dental_Management.entity.Appointment;
import com.example_SE_Dental_Management.entity.User;
import com.example_SE_Dental_Management.entity.SupplierReport;
import com.example_SE_Dental_Management.service.AppointmentService;
import com.example_SE_Dental_Management.service.UserService;
import com.example_SE_Dental_Management.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map; // <-- ADD THIS IMPORT
import java.util.HashMap; // <-- ADD THIS IMPORT
import java.util.stream.Collectors; // <-- ADD THIS IMPORT

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private AppointmentService appointmentService;
    // --- END OF FIX ---

    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin";
    }

    // --- START: REST API for User Management ---
    @GetMapping("/api/users")
    @ResponseBody
    public List<User> getAllUsers(@RequestParam(required = false) String type) {
        return userService.getAllUsers(type);
    }

    @GetMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/users")
    @ResponseBody
    public User createUser(@Valid @RequestBody User user) {
        return userService.saveUser(user);
    }

    @PutMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    // --- END: REST API for User Management ---

    // --- START: Appointment Management API ---

    // --- THIS IS THE UPDATED METHOD ---
    @GetMapping("/appointments/accepted")
    @ResponseBody
    public List<Map<String, Object>> getAcceptedAppointments() {
        // 1. Get the raw appointment list from the service
        List<Appointment> appointments = appointmentService.findAcceptedAppointments();

        // 2. Convert the list to a list of Maps to manually format the data
        // This fixes the "Invalid Date" error by converting the Java LocalDate
        // object into a simple "YYYY-MM-DD" string that JavaScript can parse.
        return appointments.stream().map(appointment -> {
            Map<String, Object> appointmentMap = new HashMap<>();

            // Add all the fields the frontend needs
            appointmentMap.put("id", appointment.getId());
            appointmentMap.put("patientName", appointment.getPatientName());
            appointmentMap.put("patientAge", appointment.getPatientAge());
            appointmentMap.put("dentistName", appointment.getDentistName());
            appointmentMap.put("patientContact", appointment.getPatientContact());
            appointmentMap.put("specialization", appointment.getSpecialization());

            // THE FIX: Convert LocalDate to a standard "YYYY-MM-DD" string
            if (appointment.getAppointmentDate() != null) {
                appointmentMap.put("appointmentDate", appointment.getAppointmentDate().toString());
            } else {
                appointmentMap.put("appointmentDate", null);
            }

            // The 'preferredTime' field is already a String, so it is sent as-is.
            // The "Invalid Date" for the time is a frontend JavaScript issue,
            // but fixing the date format here is the correct backend solution.
            appointmentMap.put("preferredTime", appointment.getPreferredTime());

            return appointmentMap;
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/appointments/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
    // --- END: Appointment Management API ---

    // --- START: Supplier Reports Management ---
    @GetMapping("/api/admin/supplier-reports")
    @ResponseBody
    public List<SupplierReport> getAllSupplierReports() {
        return supplierService.getAllReports();
    }

    @GetMapping("/api/admin/supplier-reports/{id}/download")
    public ResponseEntity<Resource> downloadSupplierReport(@PathVariable Long id) {
        SupplierReport report = supplierService.getReportById(id);
        if (report != null) {
            ByteArrayResource resource = new ByteArrayResource(report.getPdfData());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/admin/supplier-reports/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteSupplierReport(@PathVariable Long id) {
        supplierService.deleteReport(id);
        return ResponseEntity.ok().build();
    }
    // --- END: Supplier Reports Management ---
}