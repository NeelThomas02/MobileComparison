package com.example.phone_comparison_backend.service;

import com.example.phone_comparison_backend.model.SearchTerm;
import com.example.phone_comparison_backend.repository.SearchTermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SearchTermService {

    @Autowired
    private SearchTermRepository searchTermRepository;

    public void recordSearchTerm(String term) {
        if (term == null || term.trim().isEmpty()) {
            return;
        }

        term = term.toLowerCase(); // Normalize term for case-insensitive tracking

        // Check if term already exists
        Optional<SearchTerm> existingTerm = searchTermRepository.findByTerm(term);
        if (existingTerm.isPresent()) {
            SearchTerm searchTerm = existingTerm.get();
            searchTerm.setFrequency(searchTerm.getFrequency() + 1);
            searchTermRepository.save(searchTerm);
        } else {
            // Add new search term with frequency 1
            searchTermRepository.save(new SearchTerm(term, 1));
        }
    }

    public Iterable<SearchTerm> getAllSearchTerms() {
        return searchTermRepository.findAll();
    }
}
