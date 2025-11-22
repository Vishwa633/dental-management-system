package com.example_SE_Dental_Management.repository;

import com.example_SE_Dental_Management.entity.SupplierInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierInventory, Long> {
    List<SupplierInventory> findByQuantityLessThanEqual(Integer quantity);
    List<SupplierInventory> findByQuantityEquals(Integer quantity);
}

