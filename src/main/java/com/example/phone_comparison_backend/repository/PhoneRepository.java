package com.example.phone_comparison_backend.repository;

import com.example.phone_comparison_backend.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhoneRepository extends JpaRepository<Phone, Long> {

    // Existing methods
    List<Phone> findByCompany(String company); // Add query method for company
    List<Phone> findByModelContaining(String model); // Example search by model
    List<Phone> findByModelContainingAndCompany(String model, String company); // Search by model and company
    int countByModelContainingOrCompanyContaining(String model, String company);

    @Query("SELECT p.model FROM Phone p WHERE LOWER(p.model) LIKE CONCAT(LOWER(:prefix), '%')")
    List<String> findPhoneModelsByPrefix(@Param("prefix") String prefix);

}
