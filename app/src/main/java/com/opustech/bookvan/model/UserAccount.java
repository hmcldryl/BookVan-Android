package com.opustech.bookvan.model;

public class UserAccount {
    String name, email, contact_number, token;
    int points;

    public UserAccount(String name, String email, String contact_number, int points) {
        this.name = name;
        this.email = email;
        this.contact_number = contact_number;
        this.points = points;
    }

    public UserAccount(String name, String email, int points) {
        this.name = name;
        this.email = email;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
