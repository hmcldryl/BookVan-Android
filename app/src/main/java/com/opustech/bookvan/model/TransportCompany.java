package com.opustech.bookvan.model;

public class TransportCompany {
    String uid, name, description, email, contact_number, address, photo_url, banner_url;

    public TransportCompany() {
    }

    public TransportCompany(String uid, String name, String description, String email, String contact_number, String address, String photo_url, String banner_url) {
        this.uid = uid;
        this.name = name;
        this.description = description;
        this.email = email;
        this.contact_number = contact_number;
        this.address = address;
        this.photo_url = photo_url;
        this.banner_url = banner_url;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
