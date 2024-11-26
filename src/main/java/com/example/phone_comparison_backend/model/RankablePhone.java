package com.example.phone_comparison_backend.model;

public class RankablePhone implements Comparable<RankablePhone> {
    private Phone phone;
    private int frequency;

    public RankablePhone(Phone phone, int frequency) {
        this.phone = phone;
        this.frequency = frequency;
    }

    public Phone getPhone() {
        return phone;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public int compareTo(RankablePhone other) {
        // Max-heap based on frequency
        return Integer.compare(other.frequency, this.frequency);
    }
}