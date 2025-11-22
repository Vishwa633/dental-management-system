package com.example_SE_Dental_Management.service;

import com.example_SE_Dental_Management.entity.Appointment;
import com.example_SE_Dental_Management.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository AppointmentRepository;

    /**
     * Fetches all appointments with a specific status, e.g., "ACCEPTED".
     * @return A list of accepted appointments.
     */
    public List<Appointment> findAcceptedAppointments() {
        // We will add a method in the repository to find by status
        return AppointmentRepository.findByStatus("ACCEPTED");
    }

    /**
     * Deletes an appointment by its ID.
     * @param id The ID of the appointment to delete.
     */
    public void deleteAppointment(Long id) {
        AppointmentRepository.deleteById(id);
    }
}
