package com.opustech.bookvan.model;

import java.util.List;

public class TransportCompany {
    String uid, name, description, address, email, website, photo_url, banner_url;

    public TransportCompany() {
    }

    public TransportCompany(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public TransportCompany(String uid, String name, String description, String address, String email, String website, String photo_url, String banner_url) {
        this.uid = uid;
        this.name = name;
        this.description = description;
        this.address = address;
        this.email = email;
        this.website = website;
        this.photo_url = photo_url;
        this.banner_url = banner_url;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getBanner_url() {
        return banner_url;
    }

    public void setBanner_url(String banner_url) {
        this.banner_url = banner_url;
    }
}
