package com.example.phone_comparison_backend.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
public class RegistrationController {

    private static final String CSV_FILE1 = "src/main/resources/users.csv";

    // Regular expressions for validation
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,15}$"; // Username regex (3-15 characters, alphanumeric + underscore)


    static {
        try {
            if (!Files.exists(Paths.get(CSV_FILE1))) {
                try (FileWriter writer = new FileWriter(CSV_FILE1, true)) {
                    writer.append("email,password\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/register")
public ResponseEntity<String> registerUser(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
    // Error message initialization
    StringBuilder errorMessage = new StringBuilder();

    // Regex checks for input fields
    if (!Pattern.matches(USERNAME_REGEX, username)) {
        errorMessage.append("Invalid username. It must be 3-15 characters long and can only contain letters, numbers, and underscores.\n");
    }
    if (!Pattern.matches(EMAIL_REGEX, email)) {
        errorMessage.append("Invalid email format. Please provide a valid email address.\n");
    }
    if (!Pattern.matches(PASSWORD_REGEX, password)) {
        errorMessage.append("Invalid password. It must contain at least one lowercase letter, one uppercase letter, one number, and be at least 8 characters long.\n");
    }

    // Check if username or email already exists
    if (isUsernameTaken(username)) {
        errorMessage.append("Username is already taken. Please choose another one.\n");
    }
    if (isEmailTaken(email)) {
        errorMessage.append("Email is already registered. Please login.\n");
    }

    // If any errors are found, return them
    if (errorMessage.length() > 0) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
    }

    // If no errors, write the new user details to the CSV file
    try (FileWriter writer = new FileWriter(CSV_FILE1, true)) {
        writer.append(username).append(",").append(email).append(",").append(password).append("\n");
    } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    // Successfully registered, redirect to login page
    return ResponseEntity.status(HttpStatus.FOUND)
                         .header("Location", "/login.html")
                         .build();
}

// Helper method to check if username is already taken
private boolean isUsernameTaken(String username) {
    try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE1))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split(",");
            if (fields[0].equals(username)) {
                return true; // Username already taken
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return false; // Username not found
}

// Helper method to check if email is already taken
private boolean isEmailTaken(String email) {
    try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE1))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split(",");
            if (fields[1].equals(email)) {
                return true; // Email already registered
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return false; // Email not found
}

    // Login endpoint
@PostMapping("/login")
public ResponseEntity<Void> loginUser(@RequestParam String email, @RequestParam String password) {
    // Validate email and password format using regex
    if (!Pattern.matches(EMAIL_REGEX, email)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Invalid email format
    }
    if (!Pattern.matches(PASSWORD_REGEX, password)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Invalid password format
    }

    try {
        // Check credentials against CSV file
        List<String> lines = Files.readAllLines(Paths.get(CSV_FILE1));
        for (String line : lines) {
            String[] userData = line.split(",");
            if (userData.length == 3 && userData[1].equals(email) && userData[2].equals(password)) {
                // Redirect to homepage or desired page after successful login
                return ResponseEntity.status(HttpStatus.FOUND)
                                     .header("Location", "/index.html")
                                     .build();
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    // Return UNAUTHORIZED if credentials are incorrect
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
}


}
