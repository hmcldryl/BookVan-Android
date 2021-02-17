package com.opustech.bookvan.model;

import com.google.firebase.Timestamp;

public class Rental {
    String owner, van_description;
    String[] photo_url;
    int capacity;
    float price;
    Timestamp timestamp;

    public Rental(String owner, String van_description, String[] photo_url, int capacity, float price, Timestamp timestamp) {
        this.owner = owner;
        this.van_description = van_description;
        this.photo_url = photo_url;
        this.capacity = capacity;
        this.price = price;
        this.timestamp = timestamp;
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

    public String[] getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String[] photo_url) {
        this.photo_url = photo_url;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
