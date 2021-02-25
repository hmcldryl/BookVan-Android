package com.opustech.bookvan.model;

import java.util.List;

public class Rental {
    String owner, van_description, timestamp, price;
    List<String> photo_url;

    public Rental() {
    }

    public Rental(String owner, String van_description, String timestamp, List<String> photo_url, String price) {
        this.owner = owner;
        this.van_description = van_description;
        this.timestamp = timestamp;
        this.photo_url = photo_url;
        this.price = price;
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

    public List<String> getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(List<String> photo_url) {
        this.photo_url = photo_url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
