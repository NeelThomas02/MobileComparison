package com.example.phone_comparison_backend.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class FrequencyCountService {

    // In-memory map to store search term frequencies
    private Map<String, Integer> searchTermFrequencyMap = new HashMap<>();

    // Boyer-Moore algorithm for pattern matching
    public int boyerMooreCount(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        int[] badCharacterShift = new int[256]; // ASCII size

        // Initialize all occurrences as -1
        for (int i = 0; i < 256; i++) {
            badCharacterShift[i] = -1;
        }

        // Build the bad character shift table
        for (int i = 0; i < m; i++) {
            badCharacterShift[pattern.charAt(i)] = i;
        }

        int count = 0;
        int shift = 0;
        while (shift <= n - m) {
            int j = m - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j)) {
                j--;
            }

            if (j < 0) {
                count++;
                // Shift the pattern based on bad character heuristic
                shift += (shift + m < n) ? m - badCharacterShift[text.charAt(shift + m)] : 1;
            } else {
                shift += Math.max(1, j - badCharacterShift[text.charAt(shift + j)]);
            }
        }
        return count;
    }

    // Function to get the frequency of a search term
    public int getSearchTermFrequency(String searchTerm) {
        // Get the current frequency from the in-memory map
        searchTermFrequencyMap.putIfAbsent(searchTerm, 0);  // Initialize if not present
        int frequency = searchTermFrequencyMap.get(searchTerm);
        // Increment the frequency for each search term
        searchTermFrequencyMap.put(searchTerm, frequency + 1);
        return frequency + 1;  // Return the updated frequency
    }

    // Optional: method to reset the frequency map (could be useful in future scenarios)
    public void resetSearchTermFrequencies() {
        searchTermFrequencyMap.clear();  // This will reset all the frequencies when needed
    }
}
