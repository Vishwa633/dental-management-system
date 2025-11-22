package com.example_SE_Dental_Management.repository;

import com.example_SE_Dental_Management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByType(String type);

    // NEW: Find users by full name (exact match)
    List<User> findByFullName(String fullName);

    // Alternative: Find users by full name containing (if you want partial matches)
    @Query("SELECT u FROM User u WHERE u.fullName LIKE %:name%")
    List<User> findByFullNameContaining(@Param("name") String name);
}