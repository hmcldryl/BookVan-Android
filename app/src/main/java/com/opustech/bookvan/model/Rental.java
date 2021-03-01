package com.opustech.bookvan.model;

import java.util.List;

public class Rental {
    String van_model, owner, van_description, timestamp;
    float price;
    List<String> photo_url;

    public Rental() {
    }

    public Rental(String van_model, String owner, String van_description, String timestamp, float price, List<String> photo_url) {
        this.van_model = van_model;
        this.owner = owner;
        this.van_description = van_description;
        this.timestamp = timestamp;
        this.price = price;
        this.photo_url = photo_url;
    }

    public String getVan_model() {
        return van_model;
    }

    public void setVan_model(String van_model) {
        this.van_model = van_model;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getVan_description() {
        return van_description;
    }

    public void setVan_description(String van_description) {
        this.van_description = van_description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public List<String> getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(List<String> photo_url) {
        this.photo_url = photo_url;
    }
}
