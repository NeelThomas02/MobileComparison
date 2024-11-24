package com.example.phone_comparison_backend.controller;

import com.example.phone_comparison_backend.service.DatabaseWordCountService;
import com.example.phone_comparison_backend.service.FrequencyCountService;
import com.example.phone_comparison_backend.service.PhoneService;
import com.example.phone_comparison_backend.service.SpellCheckService;
import com.example.phone_comparison_backend.service.PhoneSorterService;
import com.example.phone_comparison_backend.util.WordCompletion;
import com.example.phone_comparison_backend.model.Phone;
import com.example.phone_comparison_backend.model.SearchTerm;
import com.example.phone_comparison_backend.model.SortRequest;

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
    private PhoneSorterService phoneSorterService;

    @Autowired
    private DatabaseWordCountService databaseWordCountService;

    @Autowired
    private WordCompletion wordCompletionService; // WordCompletion service for word suggestions

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

    // New endpoint for word completion
    @GetMapping("/word-completion")
    public ResponseEntity<List<String>> completeWord(@RequestParam String prefix) {
        System.out.println("Received prefix: " + prefix);  // Debugging line

        // Validate input
        if (prefix == null || prefix.trim().isEmpty()) {
            System.out.println("Invalid input: Prefix cannot be null or empty.");
            return new ResponseEntity<>(List.of("Invalid input: Prefix cannot be null or empty."), HttpStatus.BAD_REQUEST);
        }

        // Fetch phone models based on the prefix
        List<String> models = phoneService.getPhoneModelsByPrefix(prefix);
        System.out.println("Phone models fetched: " + models);  // Debugging line

        // Insert the fetched phone models into the WordCompletion service (AVL tree)
        for (String model : models) {
            wordCompletionService.insert(model);  // Insert each model into the AVL tree
        }

        // Use the WordCompletion service to find suggestions based on the prefix
        List<String> suggestions = wordCompletionService.findSuggestions(prefix);
        System.out.println("Suggestions found: " + suggestions);  // Debugging line

        // If no suggestions were found, return a message
        if (suggestions.isEmpty()) {
            return new ResponseEntity<>(List.of("No suggestions found."), HttpStatus.OK);
        }

        // Return the list of suggestions
        return new ResponseEntity<>(suggestions, HttpStatus.OK);
    }

    // Endpoint to get phone models by prefix and insert them into WordCompletion
    @GetMapping("/models")
    public List<String> getPhoneModelsByPrefix(@RequestParam String prefix) {
        System.out.println("Received prefix: " + prefix); // Debugging line
        List<String> models = phoneService.getPhoneModelsByPrefix(prefix);
        System.out.println("Returning models: " + models); // Debugging line
        return models;
    }

}
