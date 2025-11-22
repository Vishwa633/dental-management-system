package com.example_SE_Dental_Management.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "supplier_reports")
public class SupplierReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_name", nullable = false)
    private String reportName;

    @Column(length = 1000)
    private String notes;

    @Column(name = "total_items")
    private Integer totalItems;

    @Column(name = "low_stock")
    private Integer lowStock;

    @Column(name = "out_of_stock")
    private Integer outOfStock;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "upload_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;

    @Lob
    @Column(name = "pdf_data", nullable = false, columnDefinition = "VARBINARY(MAX)") // FIXED: Changed from LONGBLOB to VARBINARY(MAX)
    private byte[] pdfData;

    // Constructors, getters, and setters remain the same...
    public SupplierReport() {}

    public SupplierReport(String reportName, String notes, Integer totalItems, Integer lowStock, Integer outOfStock, byte[] pdfData) {
        this.reportName = reportName;
        this.notes = notes;
        this.totalItems = totalItems;
        this.lowStock = lowStock;
        this.outOfStock = outOfStock;
        this.pdfData = pdfData;
        this.uploadDate = new Date();
    }

    // Getters and Setters (keep all existing ones)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReportName() { return reportName; }
    public void setReportName(String reportName) { this.reportName = reportName; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }

    public Integer getLowStock() { return lowStock; }
    public void setLowStock(Integer lowStock) { this.lowStock = lowStock; }

    public Integer getOutOfStock() { return outOfStock; }
    public void setOutOfStock(Integer outOfStock) { this.outOfStock = outOfStock; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Date getUploadDate() { return uploadDate; }
    public void setUploadDate(Date uploadDate) { this.uploadDate = uploadDate; }

    public byte[] getPdfData() { return pdfData; }
    public void setPdfData(byte[] pdfData) { this.pdfData = pdfData; }
}