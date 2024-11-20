package com.example.phone_comparison_backend.controller;

import com.example.phone_comparison_backend.service.FrequencyCountService;
import com.example.phone_comparison_backend.service.PhoneService;
import com.example.phone_comparison_backend.service.SpellCheckService;
import com.example.phone_comparison_backend.model.Phone;
import com.example.phone_comparison_backend.model.SearchTerm;
import org.springframework.beans.factory.annotation.Autowired;
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
    
}
