package com.example.phone_comparison_backend.model;

import jakarta.persistence.*;


@Entity
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String model;
    @Column(name = "image_url", length = 1000)
    private String imageUrl;
    private Float price;
    private String company;
    private String productLink;

    // New fields
    private String os;
    private String ram;
    private String rom;
    private String is5G;
    private String isDualSim;
    private String bluetoothVersion;
    private String hasFastCharging;

    public Phone() {
    }

    public Phone(String model, String imageUrl, Float price, String company, String productLink) {
        this.model = model;
        this.imageUrl = imageUrl;
        this.price = price;
        this.company = company;
        this.productLink = productLink;
    }

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

    public String getProductLink() {
        return productLink;
    }

    public void setProductLink(String productLink) {
        this.productLink = productLink;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getRom() {
        return rom;
    }

    public void setRom(String rom) {
        this.rom = rom;
    }

    public String getIs5G() {
        return is5G;
    }

    public void set5G(String is5G) {
        this.is5G = is5G;
    }

    public String getIsDualSim() {
        return isDualSim;
    }

    public void setDualSim(String isDualSim) {
        this.isDualSim = isDualSim;
    }

    public String getBluetoothVersion() {
        return bluetoothVersion;
    }

    public void setBluetoothVersion(String bluetoothVersion) {
        this.bluetoothVersion = bluetoothVersion;
    }

    public String getHasFastCharging() {
        return hasFastCharging;
    }

    public void setHasFastCharging(String hasFastCharging) {
        this.hasFastCharging = hasFastCharging;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", price=" + price +
                ", company='" + company + '\'' +
                ", productLink='" + productLink + '\'' +
                ", os='" + os + '\'' +
                ", ram='" + ram + '\'' +
                ", rom='" + rom + '\'' +
                ", is5G='" + is5G + '\'' +
                ", isDualSim='" + isDualSim + '\'' +
                ", bluetoothVersion='" + bluetoothVersion + '\'' +
                ", hasFastCharging='" + hasFastCharging + '\'' +
                '}';
    }
}
