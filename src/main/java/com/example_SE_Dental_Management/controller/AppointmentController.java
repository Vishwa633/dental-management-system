package com.example_SE_Dental_Management.controller;

import com.example_SE_Dental_Management.entity.Appointment;
import com.example_SE_Dental_Management.entity.User;
import com.example_SE_Dental_Management.repository.AppointmentRepository;
import com.example_SE_Dental_Management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/book")
    public String showAppointmentForm() {
        return "Appointment";
    }

    @PostMapping("/book")
    public String bookAppointment(@RequestParam String patientName, @RequestParam int age,
                                  @RequestParam String gender, // Gender parameter
                                  @RequestParam String contactNumber, @RequestParam String dentistName,
                                  @RequestParam String preferredDate, @RequestParam String preferredTime,
                                  @RequestParam(required = false) String appointmentNotes,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {

        User currentUser = userRepository.findByUsername(userDetails.getUsername()).get();
        Appointment appointment = new Appointment();
        appointment.setPatientName(patientName);
        appointment.setPatientAge(age);
        appointment.setGender(gender); // SET THE GENDER (ONLY ONCE)
        appointment.setPatientContact(contactNumber);

        // FIX: Always store "Dr. Sarah Johnson" as dentist name regardless of selection
        appointment.setDentistName(dentistName);

        // FIX: Store the selected dentist's specialization from the display text
        // Extract specialization from the selected option's display text
        String specialization = "General Dentistry"; // default
        if (dentistName.contains("(Orthodontics)")) {
            specialization = "Orthodontics";
        } else if (dentistName.contains("(Pediatric Dentistry)")) {
            specialization = "Pediatric Dentistry";
        } else if (dentistName.contains("(Oral Surgery)")) {
            specialization = "Oral Surgery";
        } else if (dentistName.contains("(Periodontics)")) {
            specialization = "Periodontics";
        } else if (dentistName.contains("(Cosmetic Dentistry)")) {
            specialization = "Cosmetic Dentistry";
        }
        appointment.setSpecialization(specialization);

        appointment.setAppointmentDate(LocalDate.parse(preferredDate));
        appointment.setPreferredTime(preferredTime);
        appointment.setAppointmentNotes(appointmentNotes);
        appointment.setPatient(currentUser);

        appointmentRepository.save(appointment);
        redirectAttributes.addFlashAttribute("success", "Appointment booked successfully!");
        return "redirect:/index.html";
    }

    // --- THIS IS THE UPDATED METHOD ---
    @PostMapping("/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // 1. Find the appointment in the database first.
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);

        if (appointmentOptional.isPresent()) {
            Appointment appointment = appointmentOptional.get();

            // 2. Check if the status is "PENDING".
            if ("PENDING".equalsIgnoreCase(appointment.getStatus())) {
                // 3. If it is, delete it and send a success message.
                appointmentRepository.delete(appointment);
                redirectAttributes.addFlashAttribute("success", "Appointment cancelled successfully.");
            } else {
                // 4. If it's not pending, do NOT delete it and send an error message.
                redirectAttributes.addFlashAttribute("error", "Cannot cancel an appointment that has already been " + appointment.getStatus().toLowerCase() + ".");
            }
        } else {
            // Handle case where appointment is not found
            redirectAttributes.addFlashAttribute("error", "Appointment not found.");
        }

        return "redirect:/profile";
    }
}