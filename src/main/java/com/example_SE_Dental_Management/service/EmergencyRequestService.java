package com.example_SE_Dental_Management.service;

import com.example_SE_Dental_Management.entity.EmergencyRequest;
import com.example_SE_Dental_Management.repository.EmergencyRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EmergencyRequestService {

    private final EmergencyRequestRepository emergencyRequestRepository;

    @Autowired
    public EmergencyRequestService(EmergencyRequestRepository emergencyRequestRepository) {
        this.emergencyRequestRepository = emergencyRequestRepository;
    }

    public List<EmergencyRequest> getAllEmergencyRequests() {
        System.out.println("Fetching all emergency requests");
        return emergencyRequestRepository.findAllByOrderByRequestDateDesc();
    }

    public EmergencyRequest createEmergencyRequest(Map<String, String> payload) {
        System.out.println("Creating emergency request with payload: " + payload);

        EmergencyRequest request = new EmergencyRequest();
        request.setPatientName(payload.get("patientName"));
        request.setXrayType(payload.get("xrayType"));
        request.setNotes(payload.get("notes"));
        request.setCreatedBy(payload.get("createdBy") != null ? payload.get("createdBy") : "Lab Assistant");

        EmergencyRequest saved = emergencyRequestRepository.save(request);
        System.out.println("Emergency request created with ID: " + saved.getId());
        return saved;
    }

    public EmergencyRequest updateEmergencyRequest(Long id, Map<String, String> updates) {
        System.out.println("Updating emergency request ID: " + id + " with updates: " + updates);

        EmergencyRequest request = emergencyRequestRepository.findById(id)
                .orElseThrow(() -> {
                    System.err.println("Emergency request not found with ID: " + id);
                    return new RuntimeException("Emergency request not found");
                });

        if (updates.containsKey("imagePath")) {
            request.setImagePath(updates.get("imagePath"));
        }
        if (updates.containsKey("notes")) {
            request.setNotes(updates.get("notes"));
        }

        return emergencyRequestRepository.save(request);
    }

    public void deleteEmergencyRequest(Long id) {
        System.out.println("Attempting to delete emergency request ID: " + id);

        if (emergencyRequestRepository.existsById(id)) {
            emergencyRequestRepository.deleteById(id);
            System.out.println("Emergency request deleted successfully ID: " + id);
        } else {
            System.err.println("Emergency request not found for deletion ID: " + id);
            throw new RuntimeException("Emergency request not found with id: " + id);
        }
    }
}