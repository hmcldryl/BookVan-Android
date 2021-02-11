package com.opustech.bookvan.model;

public class ChatConversation {
    String name, photo_url, email, contact_number, uid;

    public ChatConversation(String name, String photo_url, String email, String contact_number, String uid) {
        this.name = name;
        this.photo_url = photo_url;
        this.email = email;
        this.contact_number = contact_number;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ChatConversation() {
    }
}
