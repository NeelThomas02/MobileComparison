package com.example.phone_comparison_backend.service;

import com.example.phone_comparison_backend.model.Phone;
import com.example.phone_comparison_backend.model.PhoneComparison;
import com.example.phone_comparison_backend.model.SearchTerm;
import com.example.phone_comparison_backend.repository.PhoneRepository;
import com.example.phone_comparison_backend.repository.SearchTermRepository;
import com.example.phone_comparison_backend.util.KMPAlgorithm;
import com.example.phone_comparison_backend.util.WordCompletion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.opencsv.CSVReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Service
public class PhoneService {

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private SearchTermRepository searchTermRepository;

    @Autowired
    private SpellCheckService spellCheckService;

    @Autowired
    private WordCompletion wordCompletion;

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
                String productLink = line.length > 4 ? line[4] : "";
                Phone phone = new Phone(model, imageUrl, price, company, productLink);
                phone.setProductLink(productLink);
                phoneList.add(phone);
            }
        }
        phoneRepository.saveAll(phoneList);
        initializeSpellCheck();
    }

    public List<Phone> findPhonesByIds(List<Long> phoneIds) {
        return phoneRepository.findAllById(phoneIds);
    }

    public PhoneComparison comparePhonesDetailed(Long id1, Long id2) {
        Phone phone1 = phoneRepository.findById(id1)
                .orElseThrow(() -> new RuntimeException("Phone with id " + id1 + " not found"));
        Phone phone2 = phoneRepository.findById(id2)
                .orElseThrow(() -> new RuntimeException("Phone with id " + id2 + " not found"));
    
        StringBuilder comparisonBuilder = new StringBuilder();
    
        // Example comparisons
        if (phone1.getPrice() < phone2.getPrice()) {
            comparisonBuilder.append(phone1.getModel()).append(" is cheaper than ").append(phone2.getModel()).append(".\n");
        } else if (phone1.getPrice() > phone2.getPrice()) {
            comparisonBuilder.append(phone1.getModel()).append(" is more expensive than ").append(phone2.getModel()).append(".\n");
        } else {
            comparisonBuilder.append("Both phones have the same price.\n");
        }
    
        // Add more comparisons based on other attributes
        if (phone1.getCompany().equals(phone2.getCompany())) {
            comparisonBuilder.append("Both phones are from the same company: ").append(phone1.getCompany()).append(".\n");
        } else {
            comparisonBuilder.append(phone1.getCompany()).append(" vs ").append(phone2.getCompany()).append(".\n");
        }
    
        return new PhoneComparison(phone1, phone2, comparisonBuilder.toString());
    }

    public Map<String, Object> comparePhones(Long phone1Id, Long phone2Id) {
    Map<String, Object> comparisonData = new HashMap<>();

    Phone phone1 = phoneRepository.findById(phone1Id).orElse(null);
    Phone phone2 = phoneRepository.findById(phone2Id).orElse(null);

    if (phone1 != null && phone2 != null) {
        comparisonData.put("Model", Map.of("phone1", phone1.getModel(), "phone2", phone2.getModel()));
        comparisonData.put("Price", Map.of("phone1", phone1.getPrice(), "phone2", phone2.getPrice()));
        comparisonData.put("Company", Map.of("phone1", phone1.getCompany(), "phone2", phone2.getCompany()));
        // Add more features as necessary
    }

    return comparisonData;
}


    private Float parsePrice(String rawPrice) {
        if (rawPrice == null || rawPrice.isEmpty()) return 0.0f;
        String sanitizedPrice = rawPrice.replaceAll("[^\\d.]", "");
        if (sanitizedPrice.indexOf('.') != sanitizedPrice.lastIndexOf('.')) {
            System.err.println("Invalid price format with multiple decimal points: " + rawPrice);
            return 0.0f;
        }
        try {
            return sanitizedPrice.isEmpty() ? 0.0f : Float.parseFloat(sanitizedPrice);
        } catch (NumberFormatException e) {
            System.err.println("Invalid price format: " + rawPrice);
            return 0.0f;
        }
    }

    public List<Phone> getAllPhones() {
        return phoneRepository.findAll();
    }

    public List<Phone> searchPhones(String model, String company) {
        if (model != null && !model.isEmpty()) {
            trackSearchTerm(model);
        }
        if (company != null && !company.isEmpty()) {
            trackSearchTerm(company);
        }
        List<Phone> phones = new ArrayList<>();
        if (model != null && company != null) {
            phones = phoneRepository.findByModelContainingAndCompany(model, company);
        } else if (model != null) {
            phones = phoneRepository.findByModelContaining(model);
        } else if (company != null) {
            phones = phoneRepository.findByCompany(company);
        } else {
            phones = phoneRepository.findAll();
        }
        return phones;
    }

    public List<Phone> sortPhonesByPrice(List<Phone> phones, boolean ascending) {
        quickSort(phones, 0, phones.size() - 1, ascending, "price");
        return phones;
    }

    public List<Phone> sortPhonesByModel(List<Phone> phones, boolean ascending) {
        quickSort(phones, 0, phones.size() - 1, ascending, "model");
        return phones;
    }

    private void quickSort(List<Phone> phones, int low, int high, boolean ascending, String sortBy) {
        if (low < high) {
            int pivotIndex = partition(phones, low, high, ascending, sortBy);
            quickSort(phones, low, pivotIndex - 1, ascending, sortBy);
            quickSort(phones, pivotIndex + 1, high, ascending, sortBy);
        }
    }

    private int partition(List<Phone> phones, int low, int high, boolean ascending, String sortBy) {
        Phone pivot = phones.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            boolean condition = false;
            if ("price".equalsIgnoreCase(sortBy)) {
                condition = ascending ? phones.get(j).getPrice() < pivot.getPrice() : phones.get(j).getPrice() > pivot.getPrice();
            }
            if ("model".equalsIgnoreCase(sortBy)) {
                condition = ascending ? phones.get(j).getModel().compareTo(pivot.getModel()) < 0 : phones.get(j).getModel().compareTo(pivot.getModel()) > 0;
            }
            if (condition) {
                i++;
                Phone temp = phones.get(i);
                phones.set(i, phones.get(j));
                phones.set(j, temp);
            }
        }
        Phone temp = phones.get(i + 1);
        phones.set(i + 1, phones.get(high));
        phones.set(high, temp);
        return i + 1;
    }

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

    public List<SearchTerm> getSearchStatistics() {
        return searchTermRepository.findAll();
    }

    public void initializeSpellCheck() {
        spellCheckService.loadVocabulary();
    }

    public List<String> getWordCompletions(String prefix) {
        return wordCompletion.findSuggestions(prefix.toLowerCase());
    }

    public int getDatabaseWordCount(String searchTerm) {
        List<Phone> allPhones = phoneRepository.findAll();
        int totalCount = 0;
        for (Phone phone : allPhones) {
            totalCount += KMPAlgorithm.countOccurrences(phone.getModel().toLowerCase(), searchTerm.toLowerCase());
            totalCount += KMPAlgorithm.countOccurrences(phone.getCompany().toLowerCase(), searchTerm.toLowerCase());
        }
        return totalCount;
    }

    public List<String> getPhoneModelsByPrefix(String prefix) {
        System.out.println("Received request to fetch phone models with prefix: " + prefix);
        if (prefix == null || prefix.trim().isEmpty()) {
            System.out.println("Prefix is null or empty. Returning empty list.");
            return List.of();
        }
        List<String> models = phoneRepository.findPhoneModelsByPrefix(prefix);
        System.out.println("Fetched phone models: " + models);
        if (models.isEmpty()) {
            System.out.println("No phone models found with the prefix: " + prefix);
        }
        for (String model : models) {
            System.out.println("Inserting model into AVL tree: " + model);
            wordCompletion.insert(model);
        }
        return models;
    }
}