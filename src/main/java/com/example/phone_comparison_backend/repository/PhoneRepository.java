package com.example.phone_comparison_backend.repository;

import com.example.phone_comparison_backend.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhoneRepository extends JpaRepository<Phone, Long> {
    List<Phone> findByCompany(String company); // Add query method for company
    List<Phone> findByModelContaining(String model); // Example search by model
    List<Phone> findByModelContainingAndCompany(String model, String company); // Search by model and company
}
