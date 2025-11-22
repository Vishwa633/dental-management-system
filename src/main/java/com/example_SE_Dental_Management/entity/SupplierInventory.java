package com.example_SE_Dental_Management.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "supplier_inventory")
public class SupplierInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    // Constructors
    public SupplierInventory() {}

    public SupplierInventory(String name, String description, Integer quantity, String image) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

}