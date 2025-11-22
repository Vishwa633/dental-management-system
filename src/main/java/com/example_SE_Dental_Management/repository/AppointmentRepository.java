package com.example_SE_Dental_Management.repository;

import com.example_SE_Dental_Management.entity.Appointment;
import com.example_SE_Dental_Management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient(User patient);

    // Find appointments by dentist name containing and status, ordered by date
    List<Appointment> findByDentistNameContainingAndStatusOrderByAppointmentDateAsc(String dentistName, String status);

    List<Appointment> findByStatus(String status);
}