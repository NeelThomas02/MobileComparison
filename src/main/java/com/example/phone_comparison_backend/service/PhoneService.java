package com.example.phone_comparison_backend.service;



import com.example.phone_comparison_backend.model.Phone;

import com.example.phone_comparison_backend.model.SearchTerm;

import com.example.phone_comparison_backend.repository.PhoneRepository;

import com.example.phone_comparison_backend.repository.SearchTermRepository;

import com.example.phone_comparison_backend.util.KMPAlgorithm;



import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.ClassPathResource;

import org.springframework.stereotype.Service;



import com.opencsv.CSVReader;



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



    @Autowired

    private SpellCheckService spellCheckService;



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

        initializeSpellCheck();  // Initialize spell check after saving phones

    }



    // Method to handle parsing of price strings

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



    // Fetch all phones from the database

    public List<Phone> getAllPhones() {

        return phoneRepository.findAll();

    }



    // Search phones by model or company and track search terms

    public List<Phone> searchPhones(String model, String company) {

        if (model != null && !model.isEmpty()) {

            trackSearchTerm(model); // Track model search

        }

        if (company != null && !company.isEmpty()) {

            trackSearchTerm(company); // Track company search

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



    // Method to sort phones by price

    public List<Phone> sortPhonesByPrice(List<Phone> phones, boolean ascending) {

        quickSort(phones, 0, phones.size() - 1, ascending, "price");

        return phones;

    }



    // Method to sort phones by model name

    public List<Phone> sortPhonesByModel(List<Phone> phones, boolean ascending) {

        quickSort(phones, 0, phones.size() - 1, ascending, "model");

        return phones;

    }



    // Quick Sort Implementation for sorting

    private void quickSort(List<Phone> phones, int low, int high, boolean ascending, String sortBy) {

        if (low < high) {

            int pivotIndex = partition(phones, low, high, ascending, sortBy);

            quickSort(phones, low, pivotIndex - 1, ascending, sortBy);

            quickSort(phones, pivotIndex + 1, high, ascending, sortBy);

        }

    }



    // Partition function for Quick Sort

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



    // Track search terms and their frequencies

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



    // Get search term statistics

    public List<SearchTerm> getSearchStatistics() {

        return searchTermRepository.findAll();

    }



    // Initialize spell check by loading the vocabulary into the Trie

    public void initializeSpellCheck() {

        spellCheckService.loadVocabulary();

    }





    // public int getDatabaseWordCount(String term) {

    //     return phoneRepository.countByModelContainingOrCompanyContaining(term, term);

    // }





     public int getDatabaseWordCount(String searchTerm) {

        List<Phone> allPhones = phoneRepository.findAll();

        int totalCount = 0;

        

        for (Phone phone : allPhones) {

            totalCount += KMPAlgorithm.countOccurrences(phone.getModel().toLowerCase(), searchTerm.toLowerCase());

            totalCount += KMPAlgorithm.countOccurrences(phone.getCompany().toLowerCase(), searchTerm.toLowerCase());

        }

        

        return totalCount;

    }
}