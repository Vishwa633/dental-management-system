// EmergencyRequestRepository.java
package com.example_SE_Dental_Management.repository;

import com.example_SE_Dental_Management.entity.EmergencyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest, Long> {
    List<EmergencyRequest> findAllByOrderByRequestDateDesc();
}