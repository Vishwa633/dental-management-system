package com.example_SE_Dental_Management.controller;

import com.example_SE_Dental_Management.entity.SupplierInventory;
import com.example_SE_Dental_Management.entity.SupplierReport;
import com.example_SE_Dental_Management.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping("/supplier/dashboard")
    public String supplierDashboard() {
        return "supplier";
    }

    // Inventory Management Endpoints
    @GetMapping("/api/supplier/items")
    @ResponseBody
    public List<SupplierInventory> getAllItems() {
        return supplierService.getAllItems();
    }

    @PostMapping("/api/supplier/items")
    @ResponseBody
    public ResponseEntity<?> saveItems(@RequestBody List<SupplierInventory> items) {
        try {
            supplierService.saveAllItems(items);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error saving items: " + e.getMessage());
        }
    }

    // ADD THIS DELETE ENDPOINT
    @DeleteMapping("/api/supplier/items/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        try {
            System.out.println("=== DELETE ITEM REQUEST ===");
            System.out.println("Item ID: " + id);

            // Get all items and find the specific one
            List<SupplierInventory> allItems = supplierService.getAllItems();
            SupplierInventory itemToDelete = allItems.stream()
                    .filter(i -> i.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (itemToDelete == null) {
                System.out.println("Item not found in database");
                return ResponseEntity.notFound().build();
            }

            System.out.println("Found item: " + itemToDelete.getName() + ", Quantity: " + itemToDelete.getQuantity());

            // Check if quantity is 0
            if (itemToDelete.getQuantity() != 0 && itemToDelete.getQuantity() != null) {
                String errorMessage = "Item '" + itemToDelete.getName() + "' has quantity " + itemToDelete.getQuantity() + ". Stock must be 0 to delete.";
                System.out.println(errorMessage);
                return ResponseEntity.badRequest().body(errorMessage);
            }

            // Perform the deletion
            supplierService.deleteItem(id);
            System.out.println("Item successfully deleted from database");

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Error deleting item: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Server error: " + e.getMessage());
        }
    }

    // PDF Report Endpoints
    @PostMapping("/supplier/upload-pdf") // Changed from /api/supplier/upload-pdf to match frontend
    @ResponseBody
    public ResponseEntity<?> uploadPdfReport(
            @RequestParam("file") MultipartFile pdfFile, // Changed from "pdfFile" to "file"
            @RequestParam("reportName") String reportName,
            @RequestParam(value = "notes", required = false) String notes) {
        // Remove the stock parameters since they're not being sent from frontend

        try {
            // Calculate stats from current inventory instead of receiving as parameters
            List<SupplierInventory> allItems = supplierService.getAllItems();
            int totalItems = allItems.size();
            int lowStock = supplierService.getLowStockItems().size();
            int outOfStock = supplierService.getOutOfStockItems().size();

            SupplierReport report = new SupplierReport();
            report.setReportName(reportName);
            report.setNotes(notes != null ? notes : "");
            report.setTotalItems(totalItems);
            report.setLowStock(lowStock);
            report.setOutOfStock(outOfStock);
            report.setPdfData(pdfFile.getBytes());
            report.setFileName(pdfFile.getOriginalFilename());
            report.setUploadDate(new java.util.Date());

            supplierService.saveReport(report);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error uploading PDF: " + e.getMessage());
        }
    }

    @GetMapping("/api/supplier/reports")
    @ResponseBody
    public List<SupplierReport> getAllReports() {
        return supplierService.getAllReports();
    }

    @GetMapping("/api/supplier/reports/{id}/download")
    public ResponseEntity<Resource> downloadReport(@PathVariable Long id) {
        SupplierReport report = supplierService.getReportById(id);
        if (report != null) {
            ByteArrayResource resource = new ByteArrayResource(report.getPdfData());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/supplier/reports/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        supplierService.deleteReport(id);
        return ResponseEntity.ok().build();
    }
}