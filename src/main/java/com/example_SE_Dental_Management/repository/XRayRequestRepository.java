package com.example_SE_Dental_Management.repository;

import com.example_SE_Dental_Management.entity.XRayRequest;
import com.example_SE_Dental_Management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface XRayRequestRepository extends JpaRepository<XRayRequest, Long> {
    // Finds all requests made by a specific dentist, ordered with the newest first
    List<XRayRequest> findByDentistOrderByRequestDateDesc(User dentist);

    // NEW METHODS FOR X-RAY LAB ASSISTANT DASHBOARD:

    // Get all pending requests (for X-ray lab assistant)
    List<XRayRequest> findByStatusOrderByRequestDateDesc(String status);

    // Get all requests regardless of dentist (for X-ray lab assistant)
    List<XRayRequest> findAllByOrderByRequestDateDesc();

    // Get emergency requests (you can add an emergency flag to entity later)
    @Query("SELECT x FROM XRayRequest x WHERE x.description LIKE '%emergency%' OR x.description LIKE '%urgent%' ORDER BY x.requestDate DESC")
    List<XRayRequest> findEmergencyRequests();
}