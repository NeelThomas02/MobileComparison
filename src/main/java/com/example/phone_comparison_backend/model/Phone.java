package com.example.phone_comparison_backend.model;

import jakarta.persistence.*;

@Entity
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String model;

    @Column(name = "image_url", length = 1000)
    private String imageUrl; // The image URL for the phone

    private Float price; // Price of the phone

    private String company; // Company name

    // Default constructor
    public Phone() {}

    // Constructor to initialize the Phone object with the required fields
    public Phone(String model, String imageUrl, Float price, String company) {
        this.model = model;
        this.imageUrl = imageUrl;
        this.price = price;
        this.company = company;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "Phone{id=" + id + ", model='" + model + "', imageUrl='" + imageUrl + "', price=" + price + ", company='" + company + "'}";
    }
}
