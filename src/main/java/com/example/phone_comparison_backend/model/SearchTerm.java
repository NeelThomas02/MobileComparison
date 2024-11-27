package com.example.phone_comparison_backend.model;

import jakarta.persistence.*;

@Entity
public class SearchTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String term;

    private int frequency;

    public SearchTerm() {}

    public SearchTerm(String term, int frequency) {
        this.term = term;
        this.frequency = frequency;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
