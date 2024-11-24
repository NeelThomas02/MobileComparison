package com.example.phone_comparison_backend.controller;



import com.example.phone_comparison_backend.service.DatabaseWordCountService;

import com.example.phone_comparison_backend.service.FrequencyCountService;

import com.example.phone_comparison_backend.service.PhoneService;

import com.example.phone_comparison_backend.service.SpellCheckService;

import com.example.phone_comparison_backend.service.PhoneSorterService; // New service for sorting

import com.example.phone_comparison_backend.model.Phone;

import com.example.phone_comparison_backend.model.SearchTerm;

import com.example.phone_comparison_backend.model.SortRequest; // New model for sort request

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;



import java.util.List;



@RestController

@RequestMapping("/phones")

public class PhoneController {



    @Autowired

    private PhoneService phoneService;



    @Autowired

    private FrequencyCountService frequencyCountService;



    @Autowired

    private SpellCheckService spellCheckService;



    @Autowired

    private PhoneSorterService phoneSorterService; // New service for sorting phones



    @Autowired

    private DatabaseWordCountService databaseWordCountService;



    // Enable CORS for all origins (can be restricted later if needed)

    @CrossOrigin(origins = "*")

    

    // Endpoint to get all phones

    @GetMapping

    public List<Phone> getAllPhones() {

        return phoneService.getAllPhones();

    }



    // Endpoint to search phones by model or company

    @GetMapping("/search")

    public List<Phone> searchPhones(@RequestParam(required = false) String model,

                                    @RequestParam(required = false) String company) {

        return phoneService.searchPhones(model, company);

    }



    // Endpoint to get search term statistics

    @GetMapping("/search-stats")

    public List<SearchTerm> getSearchStatistics() {

        return phoneService.getSearchStatistics();

    }



    // Endpoint to get frequency of a search term

    @PostMapping("/searchFrequency")

    public int getSearchTermFrequency(@RequestBody String searchTerm) {

        return frequencyCountService.getSearchTermFrequency(searchTerm);

    }



    // Endpoint to check the spelling of a search term and get suggestions

    @GetMapping("/spellcheck")

    public List<String> checkSpelling(@RequestParam String searchTerm) {

        // Validate the input

        if (searchTerm == null || searchTerm.trim().isEmpty()) {

            return List.of("Invalid input: Search term cannot be null or empty.");

        }

    

        // Convert the search term to lowercase for uniformity

        String normalizedSearchTerm = searchTerm.toLowerCase();

    

        // Check if the word exists in the Trie

        if (spellCheckService.checkIfWordExists(normalizedSearchTerm)) {

            return List.of("Word exists in the vocabulary.");

        } else {

            // If the word doesn't exist, return suggestions

            return spellCheckService.suggestWords(normalizedSearchTerm);

        }

    }



    // New endpoint to sort phones by price or model

    @PostMapping("/sort")

    public List<Phone> sortPhones(@RequestBody SortRequest sortRequest) {

        List<Phone> phones = phoneService.getAllPhones(); // Get all phones



        // Check if the list of phones is empty

        if (phones.isEmpty()) {

            return List.of(); // Return empty list if no phones are available

        }



        // Check the sorting criteria and apply the sorting

        if ("price".equalsIgnoreCase(sortRequest.getSortBy())) {

            return phoneSorterService.sortByPrice(phones, sortRequest.isAscending());

        } else if ("model".equalsIgnoreCase(sortRequest.getSortBy())) {

            return phoneSorterService.sortByModel(phones, sortRequest.isAscending());

        }



        // Handle invalid sort criteria

        throw new IllegalArgumentException("Invalid sort criteria. Please use 'price' or 'model'.");

    }

    @GetMapping("/database-word-count")

public int getDatabaseWordCount(@RequestParam String term) {

    return phoneService.getDatabaseWordCount(term);

}

}