package com.example.phone_comparison_backend.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service  // Ensure the class is a Spring bean
public class WordCompletion {

    private Node root;

    private static class Node {
        String word;
        Node left, right;
        int height;

        Node(String word) {
            this.word = word;
            this.height = 1;
        }
    }

    // Insert a word into the AVL Tree
    public void insert(String word) {
        root = insert(root, word);
    }

    // Helper function for insertion
    private Node insert(Node node, String word) {
        if (node == null) {
            return new Node(word);
        }

        if (word.compareTo(node.word) < 0) {
            node.left = insert(node.left, word);
        } else if (word.compareTo(node.word) > 0) {
            node.right = insert(node.right, word);
        } else {
            return node;
        }

        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));

        int balance = getBalance(node);

        if (balance > 1 && word.compareTo(node.left.word) < 0) {
            return rightRotate(node);
        }

        if (balance < -1 && word.compareTo(node.right.word) > 0) {
            return leftRotate(node);
        }

        if (balance > 1 && word.compareTo(node.left.word) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && word.compareTo(node.right.word) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;
        y.left = x;
        x.right = T2;
        x.height = Math.max(getHeight(x.left), getHeight(x.right)) + 1;
        y.height = Math.max(getHeight(y.left), getHeight(y.right)) + 1;
        return y;
    }

    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;
        x.right = y;
        y.left = T2;
        y.height = Math.max(getHeight(y.left), getHeight(y.right)) + 1;
        x.height = Math.max(getHeight(x.left), getHeight(x.right)) + 1;
        return x;
    }

    private int getHeight(Node node) {
        return node == null ? 0 : node.height;
    }

    private int getBalance(Node node) {
        return node == null ? 0 : getHeight(node.left) - getHeight(node.right);
    }

    // Find suggestions that start with the given prefix
    public List<String> findSuggestions(String prefix) {
        List<String> suggestions = new ArrayList<>();
        findSuggestions(root, prefix.toLowerCase(), suggestions);
        return suggestions;
    }

    private void findSuggestions(Node node, String prefix, List<String> suggestions) {
        if (node == null) {
            return;
        }

        // If the word starts with the prefix, add it to the suggestions
        if (node.word.startsWith(prefix)) {
            suggestions.add(node.word);
        }

        if (prefix.compareTo(node.word) < 0) {
            findSuggestions(node.left, prefix, suggestions);
        } else {
            findSuggestions(node.right, prefix, suggestions);
        }
    }
}
