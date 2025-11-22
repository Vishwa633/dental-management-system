package com.example_SE_Dental_Management.controller;

import com.example_SE_Dental_Management.entity.Appointment;
import com.example_SE_Dental_Management.entity.Alert;
import com.example_SE_Dental_Management.repository.AppointmentRepository;
import com.example_SE_Dental_Management.repository.AlertRepository;
import com.example_SE_Dental_Management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ReceptionistApiController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all appointments
    @GetMapping("/appointments")
    public ResponseEntity<?> getAllAppointments() {
        try {
            List<Appointment> appointments = appointmentRepository.findAll();

            // ENHANCED DEBUGGING
            System.out.println("=== DEBUG: Appointments Data ===");
            for (Appointment apt : appointments) {
                System.out.println("ID: " + apt.getId());
                System.out.println("Patient Name: " + apt.getPatientName());
                System.out.println("Contact: " + apt.getPatientContact());
                System.out.println("Dentist: " + apt.getDentistName());
                System.out.println("Date: " + apt.getAppointmentDate());
                System.out.println("Status: " + apt.getStatus());
                System.out.println("User ID: " + (apt.getPatient() != null ? apt.getPatient().getId() : "No user"));
                System.out.println("---");
            }
            System.out.println("Total appointments: " + appointments.size());

            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            System.out.println("ERROR loading appointments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load appointments: " + e.getMessage()));
        }
    }

    // Get appointment statistics
    @GetMapping("/appointments/stats")
    public ResponseEntity<?> getAppointmentStats() {
        try {
            List<Appointment> appointments = appointmentRepository.findAll();

            // Get unique dentists count
            long dentistsCount = appointments.stream()
                    .map(Appointment::getDentistName)
                    .filter(Objects::nonNull)
                    .distinct()
                    .count();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalAppointments", appointments.size());
            stats.put("acceptedAppointments",
                    appointments.stream().filter(a -> a.getStatus() != null && "ACCEPTED".equalsIgnoreCase(a.getStatus())).count());
            stats.put("pendingAppointments",
                    appointments.stream().filter(a -> a.getStatus() != null && "PENDING".equalsIgnoreCase(a.getStatus())).count());

            System.out.println("DEBUG: Stats - Total: " + appointments.size() + ", Accepted: " + stats.get("acceptedAppointments") + ", Pending: " + stats.get("pendingAppointments") + ", Dentists: " + dentistsCount);

            System.out.println("DEBUG: Stats - Total: " + appointments.size() + ", Confirmed: " + stats.get("confirmedAppointments") + ", Pending: " + stats.get("pendingAppointments") + ", Dentists: " + dentistsCount);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.out.println("ERROR loading stats: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load statistics"));
        }
    }

    // Get all unique dentist names
    @GetMapping("/appointments/dentists")
    public ResponseEntity<?> getAllDentists() {
        try {
            List<Appointment> appointments = appointmentRepository.findAll();
            List<String> dentists = appointments.stream()
                    .map(Appointment::getDentistName)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            System.out.println("DEBUG: Found " + dentists.size() + " dentists: " + dentists);
            return ResponseEntity.ok(dentists);
        } catch (Exception e) {
            System.out.println("ERROR loading dentists: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load dentists"));
        }
    }

    // Get patients for a specific dentist
// Get patients for a specific dentist (ONLY ACCEPTED appointments)
    @GetMapping("/appointments/patients/{dentistName}")
    public ResponseEntity<?> getPatientsForDentist(@PathVariable String dentistName) {
        try {
            List<Appointment> appointments = appointmentRepository.findAll();

            // Filter for only ACCEPTED appointments
            List<String> patients = appointments.stream()
                    .filter(a -> a.getDentistName() != null &&
                            a.getDentistName().equals(dentistName) &&
                            a.getStatus() != null &&
                            "ACCEPTED".equalsIgnoreCase(a.getStatus()))
                    .map(Appointment::getPatientName)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            System.out.println("DEBUG: Found " + patients.size() + " ACCEPTED patients for dentist: " + dentistName);
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            System.out.println("ERROR loading patients: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load patients"));
        }
    }

    // UPDATED: Send alerts to patients with automatic patient selection and user ID tracking
    @PostMapping("/alerts/send")
    public ResponseEntity<?> sendAlert(@RequestBody Map<String, Object> alertData) {
        try {
            String dentistName = (String) alertData.get("dentistName");
            String message = (String) alertData.get("message");

            // Auto-get patients for dentist
            List<String> patientNames = new ArrayList<>();

            System.out.println("DEBUG: Sending alert for dentist: " + dentistName);

            if (dentistName == null || dentistName.isEmpty() || message == null || message.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
            }

            // Get all patients for this dentist automatically
// Get all patients for this dentist automatically
            List<Appointment> allAppointments = appointmentRepository.findAll();
            Set<Long> uniqueUserIds = new HashSet<>();
            Set<String> uniquePatientNames = new HashSet<>();

            for (Appointment appointment : allAppointments) {
                if (dentistName.equals(appointment.getDentistName()) &&
                        appointment.getPatientName() != null &&
                        !appointment.getPatientName().isEmpty() &&
                        appointment.getStatus() != null &&
                        "ACCEPTED".equalsIgnoreCase(appointment.getStatus())) {

                    uniquePatientNames.add(appointment.getPatientName());

                    // Get user IDs
                    if (appointment.getPatient() != null) {
                        uniqueUserIds.add(appointment.getPatient().getId());
                        System.out.println("DEBUG: Found user ID " + appointment.getPatient().getId() +
                                " for patient: " + appointment.getPatientName());
                    }
                }
            }

            patientNames.addAll(uniquePatientNames);

            System.out.println("DEBUG: Found " + patientNames.size() + " patients for dentist: " + dentistName);
            System.out.println("DEBUG: Found " + uniqueUserIds.size() + " unique user IDs");

            Alert alert = new Alert();
            alert.setDentistName(dentistName);
            alert.setMessage(message);
            alert.setPatientNames(String.join(", ", patientNames));

            // Set patientUserIds
            if (!uniqueUserIds.isEmpty()) {
                String userIdsString = uniqueUserIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
                alert.setPatientUserIds(userIdsString);
                System.out.println("DEBUG: Setting patientUserIds: " + userIdsString);
            } else {
                alert.setPatientUserIds(""); // Never NULL
                System.out.println("DEBUG: No user IDs found, setting empty string");
            }

            alert.setSentDate(LocalDateTime.now());

            Alert savedAlert = alertRepository.save(alert);

            System.out.println("DEBUG: ✅ Alert saved successfully!");
            System.out.println("DEBUG: Alert ID: " + savedAlert.getId());
            System.out.println("DEBUG: User IDs: " + savedAlert.getPatientUserIds());

            return ResponseEntity.ok().body(Map.of(
                    "message", "Alert sent successfully to " + patientNames.size() + " patients of " + dentistName,
                    "alertId", savedAlert.getId(),
                    "patientsCount", patientNames.size()
            ));
        } catch (Exception e) {
            System.out.println("ERROR: Failed to send alert: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to send alert: " + e.getMessage()));
        }
    }

    // Get alert history
    @GetMapping("/alerts")
    public ResponseEntity<?> getAlertHistory() {
        try {
            List<Alert> alerts = alertRepository.findAllByOrderBySentDateDesc();
            System.out.println("DEBUG: Found " + alerts.size() + " alerts in history");
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            System.out.println("ERROR loading alert history: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load alert history"));
        }
    }

    // NEW: Get alerts for a specific user (for patient profile)
    @GetMapping("/alerts/user/{userId}")
    public ResponseEntity<?> getAlertsForUser(@PathVariable Long userId) {
        try {
            List<Alert> allAlerts = alertRepository.findAllByOrderBySentDateDesc();

            // Manual filtering for user alerts
            List<Alert> userAlerts = allAlerts.stream()
                    .filter(alert -> {
                        String patientUserIds = alert.getPatientUserIds();
                        if (patientUserIds != null && !patientUserIds.isEmpty()) {
                            String[] userIds = patientUserIds.split(",");
                            for (String idStr : userIds) {
                                if (idStr.trim().equals(userId.toString())) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            System.out.println("DEBUG: Found " + userAlerts.size() + " alerts for user ID: " + userId);
            return ResponseEntity.ok(userAlerts);
        } catch (Exception e) {
            System.out.println("ERROR loading user alerts: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load user alerts"));
        }
    }

    // DEBUG: Get all alerts (temporary for testing)
    @GetMapping("/alerts/debug")
    public ResponseEntity<?> debugAlerts() {
        try {
            List<Alert> allAlerts = alertRepository.findAllByOrderBySentDateDesc();

            System.out.println("=== DEBUG: ALL ALERTS IN DATABASE ===");
            for (Alert alert : allAlerts) {
                System.out.println("ID: " + alert.getId());
                System.out.println("Dentist: " + alert.getDentistName());
                System.out.println("Patient Names: " + alert.getPatientNames());
                System.out.println("Patient User IDs: " + alert.getPatientUserIds());
                System.out.println("Message: " + alert.getMessage());
                System.out.println("Sent Date: " + alert.getSentDate());
                System.out.println("---");
            }

            return ResponseEntity.ok(allAlerts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    // Add this method to ReceptionistApiController.java

    // Delete alert by ID
    @DeleteMapping("/alerts/{alertId}")
    public ResponseEntity<?> deleteAlert(@PathVariable Long alertId) {
        try {
            System.out.println("DEBUG: Attempting to delete alert with ID: " + alertId);

            if (!alertRepository.existsById(alertId)) {
                System.out.println("DEBUG: Alert not found with ID: " + alertId);
                return ResponseEntity.status(404).body(Map.of("error", "Alert not found"));
            }

            alertRepository.deleteById(alertId);

            System.out.println("DEBUG: ✅ Alert deleted successfully! ID: " + alertId);

            return ResponseEntity.ok().body(Map.of(
                    "message", "Alert deleted successfully",
                    "deletedId", alertId
            ));
        } catch (Exception e) {
            System.out.println("ERROR: Failed to delete alert: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete alert: " + e.getMessage()));
        }
    }
}