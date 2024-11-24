package com.example.phone_comparison_backend.util;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

@Service
public class WordCompletion {

    private Node root;

    // Node structure for AVL tree
    private static class Node {
        String word;
        Node left, right;
        int height;

        Node(String word) {
            this.word = word;
            this.height = 1; // New nodes have height 1
        }
    }

    // Insert a word into the AVL Tree
    public void insert(String word) {
        root = insert(root, word.toLowerCase()); // Insert word in lowercase to handle case-insensitivity
        System.out.println("Inserted word: " + word.toLowerCase()); // Debugging
    }

    // Helper function to insert word into the AVL tree
    private Node insert(Node node, String word) {
        if (node == null) {
            return new Node(word);  // Create a new node for the word
        }

        // Insert the word in the correct place in the tree
        if (word.compareTo(node.word) < 0) {
            node.left = insert(node.left, word);  // Insert into left subtree
        } else if (word.compareTo(node.word) > 0) {
            node.right = insert(node.right, word);  // Insert into right subtree
        } else {
            // Word already exists in the tree, no need to insert again
            return node;
        }

        // Update the height of the current node
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));

        // Balance the node if needed
        int balance = getBalance(node);

        // Balance the tree and apply rotations if needed
        if (balance > 1 && word.compareTo(node.left.word) < 0) {
            return rightRotate(node);  // Right rotation if left subtree is taller
        }
        if (balance < -1 && word.compareTo(node.right.word) > 0) {
            return leftRotate(node);  // Left rotation if right subtree is taller
        }
        if (balance > 1 && word.compareTo(node.left.word) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);  // Left-right rotation
        }
        if (balance < -1 && word.compareTo(node.right.word) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);  // Right-left rotation
        }

        return node;  // Return the unchanged node if no rotation is needed
    }

    // Helper functions for balancing the AVL tree (left and right rotations)
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

    // Find all words starting with the given prefix
    public List<String> findSuggestions(String prefix) {
        Set<String> suggestionsSet = new HashSet<>();  // Use a Set to ensure uniqueness
        findSuggestions(root, prefix.toLowerCase(), suggestionsSet);  // Pass prefix in lowercase for consistency
        System.out.println("Total unique suggestions found: " + suggestionsSet.size()); // Debugging
        return new ArrayList<>(suggestionsSet);  // Convert the Set back to a List
    }

    // Helper function to find suggestions starting with the given prefix
    private void findSuggestions(Node node, String prefix, Set<String> suggestions) {
        if (node == null) {
            return;  // Base case: return if node is null
        }

        // Debugging: print the current node being processed
        System.out.println("Processing node: " + node.word);

        // Only process nodes that may contain words starting with the prefix
        if (node.word.startsWith(prefix)) {
            suggestions.add(node.word);  // Add to suggestions if it matches the prefix
            System.out.println("Added suggestion: " + node.word);  // Debugging
        }

        // Search left subtree if the prefix is lexicographically less than the node word
        if (prefix.compareTo(node.word) < 0) {
            System.out.println("Going left from node: " + node.word);  // Debugging
            findSuggestions(node.left, prefix, suggestions);
        }

        // Search right subtree if the prefix is lexicographically greater than the node word
        if (prefix.compareTo(node.word) > 0) {
            System.out.println("Going right from node: " + node.word);  // Debugging
            findSuggestions(node.right, prefix, suggestions);
        }

        // Additionally, always explore both subtrees if the current node matches the prefix
        if (node.word.startsWith(prefix)) {
            if (node.left != null) {
                System.out.println("Exploring left subtree of " + node.word);  // Debugging
                findSuggestions(node.left, prefix, suggestions);
            }
            if (node.right != null) {
                System.out.println("Exploring right subtree of " + node.word);  // Debugging
                findSuggestions(node.right, prefix, suggestions);
            }
        }
    }
}
