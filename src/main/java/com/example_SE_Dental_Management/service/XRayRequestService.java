package com.example_SE_Dental_Management.service;

import com.example_SE_Dental_Management.entity.XRayRequest;
import com.example_SE_Dental_Management.entity.User;
import com.example_SE_Dental_Management.DTO.XRayRequestDTO;
import com.example_SE_Dental_Management.repository.XRayRequestRepository;
import com.example_SE_Dental_Management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class XRayRequestService {

    @Autowired
    private XRayRequestRepository xRayRequestRepository;

    @Autowired
    private UserRepository userRepository;

    public List<XRayRequest> getRequestsForDentist(UserDetails userDetails) {
        User dentist = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Dentist not found"));
        return xRayRequestRepository.findByDentistOrderByRequestDateDesc(dentist);
    }

    public XRayRequest createXRayRequest(Map<String, String> payload, UserDetails userDetails) {
        try {
            User dentist = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Dentist not found"));

            XRayRequest request = new XRayRequest();
            request.setPatientName(payload.get("patientName"));
            request.setDescription(payload.get("description"));
            request.setRequestDate(java.time.LocalDate.now());
            request.setStatus("PENDING");
            request.setDentist(dentist);

            return xRayRequestRepository.save(request);
        } catch (Exception e) {
            System.err.println("ERROR CREATING X-RAY REQUEST: " + e.getMessage());
            throw new RuntimeException("Server error while creating request: " + e.getMessage());
        }
    }

    // DTO METHODS FOR X-RAY DASHBOARD
    public List<XRayRequestDTO> getPendingRequests() {
        List<XRayRequest> requests = xRayRequestRepository.findByStatusOrderByRequestDateDesc("PENDING");
        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<XRayRequestDTO> getProcessedRequests() {
        List<XRayRequest> requests = xRayRequestRepository.findByStatusOrderByRequestDateDesc("PROCESSED");
        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<XRayRequestDTO> getEmergencyRequests() {
        return new ArrayList<>();
    }

    public XRayRequest updateRequest(Long id, Map<String, String> updates) {
        XRayRequest request = xRayRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (updates.containsKey("status")) {
            request.setStatus(updates.get("status"));
        }
        if (updates.containsKey("imagePath")) {
            request.setImagePath(updates.get("imagePath"));
        }
        if (updates.containsKey("notes")) {
            request.setNotes(updates.get("notes"));
        }

        return xRayRequestRepository.save(request);
    }

    public void deleteRequest(Long id) {
        if (xRayRequestRepository.existsById(id)) {
            xRayRequestRepository.deleteById(id);
        } else {
            throw new RuntimeException("Request not found with id: " + id);
        }
    }

    public XRayRequest createEmergencyRequest(Map<String, String> payload) {
        XRayRequest request = new XRayRequest();
        request.setPatientName(payload.get("patientName"));
        request.setDescription(payload.get("notes") != null ? payload.get("notes") : "Emergency request");
        request.setRequestDate(java.time.LocalDate.now());
        request.setStatus("PENDING");
        return xRayRequestRepository.save(request);
    }

    private XRayRequestDTO convertToDTO(XRayRequest request) {
        String dentistName = request.getDentist() != null ?
                "Dr. " + request.getDentist().getFullName() : "Unknown Dentist";

        return new XRayRequestDTO(
                request.getId(),
                request.getPatientName(),
                request.getDescription(),
                request.getRequestDate(),
                request.getStatus(),
                request.getImagePath(),
                dentistName
        );
    }
}