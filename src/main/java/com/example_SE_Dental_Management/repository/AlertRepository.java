package com.example_SE_Dental_Management.repository;

import com.example_SE_Dental_Management.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    @Query("SELECT a FROM Alert a ORDER BY a.sentDate DESC")
    List<Alert> findAllByOrderBySentDateDesc();

    // Add this method for deleting alerts
    void deleteById(Long id);
}