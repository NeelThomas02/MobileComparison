package com.example.phone_comparison_backend.service;

import com.example.phone_comparison_backend.model.Phone;
import com.example.phone_comparison_backend.model.SearchTerm;
import com.example.phone_comparison_backend.repository.PhoneRepository;
import com.example.phone_comparison_backend.repository.SearchTermRepository;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PhoneService {

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private SearchTermRepository searchTermRepository;

    // Method to load phones from CSV
    public void loadPhonesFromCsv() throws Exception {
        List<Phone> phoneList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(
                new ClassPathResource("phones.csv").getInputStream()))) {

            String[] line;
            csvReader.readNext(); // Skip header

            while ((line = csvReader.readNext()) != null) {
                String model = line[0];
                String imageUrl = line[1];
                Float price = parsePrice(line[2]);
                String company = line[3];

                Phone phone = new Phone(model, imageUrl, price, company);
                phoneList.add(phone);
            }
        }
        phoneRepository.saveAll(phoneList);
    }

    // Method to handle parsing of price strings
    private Float parsePrice(String rawPrice) {
        if (rawPrice == null || rawPrice.isEmpty()) return 0.0f;
        rawPrice = rawPrice.replaceAll("[^\\d.]", "");
        return rawPrice.isEmpty() ? 0.0f : Float.parseFloat(rawPrice);
    }

    // Method to get all phones
    public List<Phone> getAllPhones() {
        return phoneRepository.findAll();
    }

    // Method to search phones by model or company and track search terms
    public List<Phone> searchPhones(String model, String company) {
        if (model != null && !model.isEmpty()) {
            trackSearchTerm(model); // Track model search
        }
        if (company != null && !company.isEmpty()) {
            trackSearchTerm(company); // Track company search
        }

        if (model != null && company != null) {
            return phoneRepository.findByModelContainingAndCompany(model, company);
        } else if (model != null) {
            return phoneRepository.findByModelContaining(model);
        } else if (company != null) {
            return phoneRepository.findByCompany(company);
        }
        return phoneRepository.findAll();
    }

    // Method to track search terms and their frequencies
    private void trackSearchTerm(String term) {
        Optional<SearchTerm> existingTerm = searchTermRepository.findByTerm(term);
        if (existingTerm.isPresent()) {
            SearchTerm searchTerm = existingTerm.get();
            searchTerm.setFrequency(searchTerm.getFrequency() + 1);
            searchTermRepository.save(searchTerm);
        } else {
            searchTermRepository.save(new SearchTerm(term, 1));
        }
    }

    // Method to get search term statistics
    public List<SearchTerm> getSearchStatistics() {
        return searchTermRepository.findAll();
    }
}
