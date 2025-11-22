package com.example_SE_Dental_Management.service;

import com.example_SE_Dental_Management.entity.SupplierInventory;
import com.example_SE_Dental_Management.entity.SupplierReport;
import com.example_SE_Dental_Management.repository.SupplierRepository;
import com.example_SE_Dental_Management.repository.SupplierReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierReportRepository supplierReportRepository;

    // Inventory Management Methods
    public List<SupplierInventory> getAllItems() {
        return supplierRepository.findAll();
    }

    public void saveAllItems(List<SupplierInventory> items) {
        supplierRepository.saveAll(items);
    }

    public SupplierInventory saveItem(SupplierInventory item) {
        return supplierRepository.save(item);
    }

    public void deleteItem(Long id) {
        System.out.println("Service: Deleting item with ID: " + id);
        try {
            supplierRepository.deleteById(id);
            System.out.println("Service: Item deleted successfully");
        } catch (Exception e) {
            System.out.println("Service: Error deleting item: " + e.getMessage());
            throw e; // Re-throw to let controller handle it
        }
    }

    public List<SupplierInventory> getLowStockItems() {
        return supplierRepository.findByQuantityLessThanEqual(5);
    }

    public List<SupplierInventory> getOutOfStockItems() {
        return supplierRepository.findByQuantityEquals(0);
    }

    // Report Management Methods
    public void saveReport(SupplierReport report) {
        supplierReportRepository.save(report);
    }

    public List<SupplierReport> getAllReports() {
        return supplierReportRepository.findAllByOrderByUploadDateDesc();
    }

    public SupplierReport getReportById(Long id) {
        Optional<SupplierReport> report = supplierReportRepository.findById(id);
        return report.orElse(null);
    }

    public void deleteReport(Long id) {
        supplierReportRepository.deleteById(id);
    }
}