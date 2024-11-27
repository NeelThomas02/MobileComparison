package com.example.phone_comparison_backend.model;

public class PhoneComparison {
    private Phone phone1;
    private Phone phone2;
    private String comparisonResult;

    public PhoneComparison(Phone phone1, Phone phone2, String comparisonResult) {
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.comparisonResult = comparisonResult;
    }

    // Getters and Setters
    public Phone getPhone1() {
        return phone1;
    }

    public void setPhone1(Phone phone1) {
        this.phone1 = phone1;
    }

    public Phone getPhone2() {
        return phone2;
    }

    public void setPhone2(Phone phone2) {
        this.phone2 = phone2;
    }

    public String getComparisonResult() {
        return comparisonResult;
    }

    public void setComparisonResult(String comparisonResult) {
        this.comparisonResult = comparisonResult;
    }
}