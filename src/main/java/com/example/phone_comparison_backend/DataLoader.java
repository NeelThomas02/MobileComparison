package com.example.phone_comparison_backend;

import com.example.phone_comparison_backend.service.PhoneService;
import com.example.phone_comparison_backend.repository.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private PhoneService phoneService;

    @Autowired
    private PhoneRepository phoneRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if the database is empty before loading data from CSV
        if (phoneRepository.count() == 0) {
            phoneService.loadPhonesFromCsv();
            System.out.println("Data loaded successfully from CSV to database.");
        } else {
            System.out.println("Data already exists in the database. Skipping CSV loading.");
        }
    }
}
