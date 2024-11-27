package com.example.phone_comparison_backend.model;

public class SortRequest {

    private String sortBy;  // price or model
    private boolean ascending;  // true for ascending, false for descending

    // Getters and setters
    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
}
