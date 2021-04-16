package com.opustech.bookvan.model;

import androidx.annotation.NonNull;

public class SystemSchedule {
    String route_from, route_to, category;
    double price;

    public SystemSchedule() {
    }

    public SystemSchedule(String route_from, String route_to, String category, double price) {
        this.route_from = route_from;
        this.route_to = route_to;
        this.category = category;
        this.price = price;
    }

    public SystemSchedule(String route_from, String route_to, double price) {
        this.route_from = route_from;
        this.route_to = route_to;
        this.price = price;
    }

    @NonNull
    @Override
    public String toString() {
        return route_from + " to " + route_to;
    }

    public String getRoute_from() {
        return route_from;
    }

    public void setRoute_from(String route_from) {
        this.route_from = route_from;
    }

    public String getRoute_to() {
        return route_to;
    }

    public void setRoute_to(String route_to) {
        this.route_to = route_to;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
