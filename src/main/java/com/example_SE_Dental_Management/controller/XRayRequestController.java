package com.example_SE_Dental_Management.controller;

import com.example_SE_Dental_Management.entity.EmergencyRequest;
import com.example_SE_Dental_Management.entity.XRayRequest;
import com.example_SE_Dental_Management.DTO.XRayRequestDTO;
import com.example_SE_Dental_Management.repository.EmergencyRequestRepository;
import com.example_SE_Dental_Management.service.EmergencyRequestService;
import com.example_SE_Dental_Management.service.XRayRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/xray-requests")
public class XRayRequestController {

    private final XRayRequestService xRayRequestService;
    private final EmergencyRequestService emergencyRequestService;
    private final EmergencyRequestRepository emergencyRequestRepository;

    @Autowired
    public XRayRequestController(XRayRequestService xRayRequestService,
                                 EmergencyRequestService emergencyRequestService,
                                 EmergencyRequestRepository emergencyRequestRepository) {
        this.xRayRequestService = xRayRequestService;
        this.emergencyRequestService = emergencyRequestService;
        this.emergencyRequestRepository = emergencyRequestRepository;
    }

    // ========== REGULAR X-RAY REQUEST METHODS ==========

    @GetMapping("/dentist")
    public ResponseEntity<List<XRayRequest>> getRequestsForDentist(@AuthenticationPrincipal UserDetails userDetails) {
        List<XRayRequest> requests = xRayRequestService.getRequestsForDentist(userDetails);
        return ResponseEntity.ok(requests);
    }

    @PostMapping
    public ResponseEntity<?> createXRayRequest(@RequestBody Map<String, String> payload, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            XRayRequest savedRequest = xRayRequestService.createXRayRequest(payload, userDetails);
            return ResponseEntity.ok(savedRequest);
        } catch (Exception e) {
            System.err.println("ERROR CREATING X-RAY REQUEST: " + e.getMessage());
            return ResponseEntity.status(500).body("Server error while creating request: " + e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<XRayRequestDTO>> getPendingRequests() {
        try {
            List<XRayRequestDTO> pendingRequests = xRayRequestService.getPendingRequests();
            return ResponseEntity.ok(pendingRequests);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @GetMapping("/processed")
    public ResponseEntity<List<XRayRequestDTO>> getProcessedRequests() {
        try {
            List<XRayRequestDTO> processedRequests = xRayRequestService.getProcessedRequests();
            return ResponseEntity.ok(processedRequests);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRequest(@PathVariable Long id,
                                           @RequestParam(value = "image", required = false) MultipartFile imageFile,
                                           @RequestParam(value = "notes", required = false) String notes,
                                           @RequestParam(value = "status", required = false) String status) {
        try {
            Map<String, String> updates = new HashMap<>();

            if (status != null) {
                updates.put("status", status);
            }
            if (notes != null) {
                updates.put("notes", notes);
            }
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = storeXRayImage(imageFile, id);
                updates.put("imagePath", imagePath);
                updates.put("status", "PROCESSED");
            }

            XRayRequest updatedRequest = xRayRequestService.updateRequest(id, updates);
            return ResponseEntity.ok(updatedRequest);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating request: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable Long id) {
        try {
            xRayRequestService.deleteRequest(id);
            return ResponseEntity.ok("Request deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting request: " + e.getMessage());
        }
    }

    // ========== EMERGENCY REQUEST METHODS ==========

    @GetMapping("/emergency")
    public ResponseEntity<List<EmergencyRequest>> getEmergencyRequests() {
        try {
            List<EmergencyRequest> emergencyRequests = emergencyRequestService.getAllEmergencyRequests();
            return ResponseEntity.ok(emergencyRequests);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @PostMapping("/emergency")
    public ResponseEntity<?> createEmergencyRequest(
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam("patientName") String patientName,
            @RequestParam("xrayType") String xrayType,
            @RequestParam(value = "notes", required = false) String notes,
            @RequestParam(value = "createdBy", required = false) String createdBy) {

        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("patientName", patientName);
            payload.put("xrayType", xrayType);
            payload.put("notes", notes != null ? notes : "");
            payload.put("createdBy", createdBy != null ? createdBy : "Lab Assistant");

            EmergencyRequest savedRequest = emergencyRequestService.createEmergencyRequest(payload);

            // Handle image upload if provided
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = storeEmergencyXRayImage(imageFile, savedRequest.getId());

                // Update the emergency request with image path
                Map<String, String> updates = new HashMap<>();
                updates.put("imagePath", imagePath);
                emergencyRequestService.updateEmergencyRequest(savedRequest.getId(), updates);
            }

            return ResponseEntity.ok(savedRequest);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error while creating emergency request: " + e.getMessage());
        }
    }

    @PutMapping("/emergency/{id}")
    public ResponseEntity<?> updateEmergencyRequest(@PathVariable Long id,
                                                    @RequestParam(value = "image", required = false) MultipartFile imageFile,
                                                    @RequestParam(value = "notes", required = false) String notes) {
        try {
            Map<String, String> updates = new HashMap<>();

            if (notes != null) {
                updates.put("notes", notes);
            }
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = storeEmergencyXRayImage(imageFile, id);
                updates.put("imagePath", imagePath);
            }

            EmergencyRequest updatedRequest = emergencyRequestService.updateEmergencyRequest(id, updates);
            return ResponseEntity.ok(updatedRequest);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating emergency request: " + e.getMessage());
        }
    }

    @DeleteMapping("/emergency/{id}")
    public ResponseEntity<?> deleteEmergencyRequest(@PathVariable Long id) {
        try {
            System.out.println("Received DELETE request for emergency request ID: " + id);

            // Check if the emergency request exists first
            if (!emergencyRequestRepository.existsById(id)) {
                System.err.println("Emergency request not found for deletion: " + id);
                return ResponseEntity.status(404).body("Emergency request not found with id: " + id);
            }

            emergencyRequestService.deleteEmergencyRequest(id);
            System.out.println("Successfully deleted emergency request ID: " + id);
            return ResponseEntity.ok("Emergency request deleted successfully");

        } catch (Exception e) {
            System.err.println("Error deleting emergency request ID: " + id + " - " + e.getMessage());
            return ResponseEntity.status(500).body("Error deleting emergency request: " + e.getMessage());
        }
    }

    // ========== FILE STORAGE METHODS ==========

    private String storeXRayImage(MultipartFile imageFile, Long requestId) throws IOException {
        try {
            String projectRoot = System.getProperty("user.dir");
            String uploadDir = projectRoot + File.separator + "uploads" + File.separator + "xrays" + File.separator;

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    throw new IOException("Failed to create directory: " + uploadDir);
                }
                System.out.println("Created directory: " + directory.getAbsolutePath());
            }

            String originalFilename = imageFile.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);

            String filename = "xray_" + requestId + "_" + System.currentTimeMillis() + fileExtension;
            String filePath = uploadDir + filename;
            File dest = new File(filePath);
            imageFile.transferTo(dest);

            System.out.println("File successfully saved to: " + dest.getAbsolutePath());
            System.out.println("File size: " + dest.length() + " bytes");

            return "/xray-images/" + filename;

        } catch (Exception e) {
            System.err.println("Error storing X-ray image: " + e.getMessage());
            throw new IOException("Failed to store X-ray image: " + e.getMessage(), e);
        }
    }

    private String storeEmergencyXRayImage(MultipartFile imageFile, Long requestId) throws IOException {
        try {
            String projectRoot = System.getProperty("user.dir");
            String uploadDir = projectRoot + File.separator + "uploads" + File.separator + "emergency-xrays" + File.separator;

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    throw new IOException("Failed to create directory: " + uploadDir);
                }
            }

            String originalFilename = imageFile.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);

            String filename = "emergency_" + requestId + "_" + System.currentTimeMillis() + fileExtension;
            String filePath = uploadDir + filename;
            File dest = new File(filePath);
            imageFile.transferTo(dest);

            return "/emergency-xray-images/" + filename;
        } catch (Exception e) {
            throw new IOException("Failed to store emergency X-ray image: " + e.getMessage(), e);
        }
    }

    // Extracted method for file extension
    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return ".jpg";
    }

    // Keep your existing test method
    @GetMapping("/test-upload-path")
    public ResponseEntity<String> testUploadPath() {
        String projectRoot = System.getProperty("user.dir");
        String uploadDir = projectRoot + File.separator + "uploads" + File.separator + "xrays" + File.separator;

        File directory = new File(uploadDir);
        boolean exists = directory.exists();
        boolean canWrite = directory.canWrite();

        String debugInfo = String.format(
                "Project Root: %s\nUpload Dir: %s\nDirectory Exists: %s\nCan Write: %s\nAbsolute Path: %s",
                projectRoot, uploadDir, exists, canWrite, directory.getAbsolutePath()
        );

        return ResponseEntity.ok(debugInfo);
    }
}