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
import java.util.stream.Collectors;


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

                // New fields
                String os = line.length > 5 ? line[5] : "";
                String ram = line.length > 6 ? line[6] : "";
                String rom = line.length > 7 ? line[7] : "";
                String is5G = line.length > 8 ? line[8].trim().equalsIgnoreCase("Supported") ? "Yes" : "No" : "No";
            String isDualSim = line.length > 9 ? line[9].trim().equalsIgnoreCase("Supported") ? "Yes" : "No" : "No";
            String hasFastCharging = line.length > 11 ? line[11].trim().equalsIgnoreCase("Supported") ? "Yes" : "No" : "No";

            // Bluetooth version and other fields
            String bluetoothVersion = line.length > 10 ? line[10].trim().replace(",", ";") : "";

                Phone phone = new Phone(model, imageUrl, price, company, productLink);
                phone.setOs(os);
                phone.setRam(ram);
                phone.setRom(rom);
                phone.set5G(is5G);
                phone.setDualSim(isDualSim);
                phone.setBluetoothVersion(bluetoothVersion);
                phone.setHasFastCharging(hasFastCharging);

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

        // Compare OS
        if (!phone1.getOs().equals(phone2.getOs())) {
            comparisonBuilder.append("Different OS: ").append(phone1.getOs()).append(" vs ").append(phone2.getOs()).append(".\n");
        } else {
            comparisonBuilder.append("Same OS: ").append(phone1.getOs()).append(".\n");
        }

        // Compare RAM
        if (!phone1.getRam().equals(phone2.getRam())) {
            comparisonBuilder.append("Different RAM: ").append(phone1.getRam()).append(" vs ").append(phone2.getRam()).append(".\n");
        } else {
            comparisonBuilder.append("Same RAM: ").append(phone1.getRam()).append(".\n");
        }

        // Compare ROM
        if (!phone1.getRom().equals(phone2.getRom())) {
            comparisonBuilder.append("Different ROM: ").append(phone1.getRom()).append(" vs ").append(phone2.getRom()).append(".\n");
        } else {
            comparisonBuilder.append("Same ROM: ").append(phone1.getRom()).append(".\n");
        }

        // Compare 5G capability
        if (!phone1.getIs5G().equals(phone2.getIs5G())) {
            comparisonBuilder.append("5G support differs.\n");
        } else {
            comparisonBuilder.append("Both support 5G.\n");
        }

        // Compare Dual SIM capability
        if (!phone1.getIsDualSim().equals(phone2.getIsDualSim())) {
            comparisonBuilder.append("Dual SIM support differs.\n");
        } else {
            comparisonBuilder.append("Both support Dual SIM.\n");
        }

        // Compare Bluetooth version
        if (!phone1.getBluetoothVersion().equals(phone2.getBluetoothVersion())) {
            comparisonBuilder.append("Different Bluetooth Version: ").append(phone1.getBluetoothVersion())
                    .append(" vs ").append(phone2.getBluetoothVersion()).append(".\n");
        } else {
            comparisonBuilder.append("Same Bluetooth Version: ").append(phone1.getBluetoothVersion()).append(".\n");
        }

        // Compare Fast Charging capability
        if (!phone1.getHasFastCharging().equals(phone2.getHasFastCharging())) {
            comparisonBuilder.append("Fast Charging support differs.\n");
        } else {
            comparisonBuilder.append("Both support Fast Charging.\n");
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
        comparisonData.put("OS", Map.of("phone1", phone1.getOs(), "phone2", phone2.getOs()));
        comparisonData.put("RAM", Map.of("phone1", phone1.getRam(), "phone2", phone2.getRam()));
        comparisonData.put("ROM", Map.of("phone1", phone1.getRom(), "phone2", phone2.getRom()));
        comparisonData.put("5G Support", Map.of("phone1", phone1.getIs5G(), "phone2", phone2.getIs5G()));
        comparisonData.put("Dual SIM Support", Map.of("phone1", phone1.getIsDualSim(), "phone2", phone2.getIsDualSim()));
        comparisonData.put("Bluetooth Version", Map.of("phone1", phone1.getBluetoothVersion(), "phone2", phone2.getBluetoothVersion()));
        comparisonData.put("Fast Charging Support", Map.of("phone1", phone1.getHasFastCharging(), "phone2", phone2.getHasFastCharging()));
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

    public List<Phone> searchAndRankPhones(String searchTerm) {
        List<Phone> allPhones = phoneRepository.findAll();
        Map<Phone, Integer> frequencyMap = new HashMap<>();

        // Count occurrences of the search term in each phone's model and company
        for (Phone phone : allPhones) {
            int count = countOccurrences(phone.getModel(), searchTerm) + countOccurrences(phone.getCompany(), searchTerm);
            frequencyMap.put(phone, count);
        }

        // Sort phones based on frequency in descending order
        return frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 0) // Only include phones with non-zero occurrences
                .sorted(Map.Entry.<Phone, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private int countOccurrences(String text, String searchTerm) {
        if (text == null || searchTerm == null || searchTerm.isEmpty()) {
            return 0;
        }
        return (text.toLowerCase().length() - text.toLowerCase().replace(searchTerm.toLowerCase(), "").length()) / searchTerm.length();
    }
}