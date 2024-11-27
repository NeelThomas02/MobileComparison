package com.example.phone_comparison_backend.service;

import com.example.phone_comparison_backend.model.Phone;
import com.example.phone_comparison_backend.repository.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class SpellCheckService {

    @Autowired
    private PhoneRepository phoneRepository;

    private TrieNode root;

    public SpellCheckService() {
        this.root = new TrieNode();
    }

    // Load the vocabulary into the Trie from the database
    public void loadVocabulary() {
        List<Phone> phones = phoneRepository.findAll();
        for (Phone phone : phones) {
            insertWord(phone.getModel().toLowerCase());
            insertWord(phone.getCompany().toLowerCase()); // Load brand names too
        }
        // Print all words loaded into the Trie
        System.out.println("Words loaded into the Trie:");
        List<String> allWords = getAllWordsFromTrie();
        for (String word : allWords) {
            System.out.println(word);
        }
    }

    // Insert a word into the Trie
    private void insertWord(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (!node.containsKey(c)) {
                node.put(c, new TrieNode());
            }
            node = node.get(c);
        }
        node.setEndOfWord(true);
    }

    // Method to check if a word exists in the Trie
    public boolean checkIfWordExists(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (!node.containsKey(c)) {
                return false; // If a character is missing, the word doesn't exist
            }
            node = node.get(c);
        }
        return node.isEndOfWord(); // Return true only if it's the end of a valid word
    }

    // Method to retrieve all words in the Trie
    public List<String> getAllWordsFromTrie() {
        List<String> result = new ArrayList<>();
        collectWords(root, new StringBuilder(), result);
        return result;
    }

    private void collectWords(TrieNode node, StringBuilder prefix, List<String> result) {
        if (node.isEndOfWord()) {
            result.add(prefix.toString());
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            prefix.append(entry.getKey());
            collectWords(entry.getValue(), prefix, result);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    // Suggest words and print edit distances for debugging
    public List<String> suggestWords(String word) {
        List<String> suggestions = new ArrayList<>();
        int minDistance = Integer.MAX_VALUE;
        String closestMatch = null;

        // List to hold phones related to the closest match
        List<Phone> matchingPhones = new ArrayList<>();

        List<Phone> phones = phoneRepository.findAll();
        System.out.println("Calculating edit distances for word: " + word);

        for (Phone phone : phones) {
            String phoneModel = phone.getModel().toLowerCase();
            String phoneCompany = phone.getCompany().toLowerCase();

            // Calculate edit distance for model and company
            int modelDistance = calculateEditDistance(word.toLowerCase(), phoneModel);
            int companyDistance = calculateEditDistance(word.toLowerCase(), phoneCompany);

            System.out.println("Edit distance between '" + word + "' and model '" + phoneModel + "': " + modelDistance);
            System.out.println("Edit distance between '" + word + "' and brand '" + phoneCompany + "': " + companyDistance);

            if (modelDistance < minDistance) {
                minDistance = modelDistance;
                closestMatch = phoneModel;
                matchingPhones.clear(); // Clear previously matched phones
                matchingPhones.add(phone); // Add the matched phone
            } else if (modelDistance == minDistance) {
                matchingPhones.add(phone); // Add this phone to the matching phones list
            }

            if (companyDistance < minDistance) {
                minDistance = companyDistance;
                closestMatch = phoneCompany;
                matchingPhones.clear(); // Clear previously matched phones
                matchingPhones.add(phone); // Add the matched phone
            } else if (companyDistance == minDistance) {
                matchingPhones.add(phone); // Add this phone to the matching phones list
            }
        }

        // If a closest match is found, return suggestions and matching phones
        if (matchingPhones.size() > 0) {
            for (Phone phone : matchingPhones) {
                suggestions.add(phone.getModel()); // Add matching phone models to the suggestions
            }
        }

        System.out.println("Closest match(es): ");
        for (Phone phone : matchingPhones) {
            System.out.println("Model: " + phone.getModel() + ", Brand: " + phone.getCompany());
        }

        return suggestions;
    }

    // Calculate the edit distance (Levenshtein distance)
    private int calculateEditDistance(String word1, String word2) {
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];

        for (int i = 0; i <= word1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= word2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= word1.length(); i++) {
            for (int j = 1; j <= word2.length(); j++) {
                int cost = (word1.charAt(i - 1) == word2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }

        return dp[word1.length()][word2.length()];
    }

    // Trie Node class
    private static class TrieNode {
        private Map<Character, TrieNode> children;
        private boolean isEndOfWord;

        public TrieNode() {
            children = new HashMap<>();
            isEndOfWord = false;
        }

        public boolean containsKey(char c) {
            return children.containsKey(c);
        }

        public TrieNode get(char c) {
            return children.get(c);
        }

        public void put(char c, TrieNode node) {
            children.put(c, node);
        }

        public boolean isEndOfWord() {
            return isEndOfWord;
        }

        public void setEndOfWord(boolean endOfWord) {
            isEndOfWord = endOfWord;
        }
    }
}
