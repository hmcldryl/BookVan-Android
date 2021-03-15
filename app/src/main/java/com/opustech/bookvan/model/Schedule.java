package com.opustech.bookvan.model;

public class Schedule {
    String time_queue, time_depart, van_company_uid, route_from, route_to;
    float price;

    public Schedule() {
    }

    public Schedule(String route_from, String route_to, float price) {
        this.route_from = route_from;
        this.route_to = route_to;
        this.price = price;
    }

    public Schedule(String time_queue, String time_depart, String van_company_uid, String route_from, String route_to, float price) {
        this.time_queue = time_queue;
        this.time_depart = time_depart;
        this.van_company_uid = van_company_uid;
        this.route_from = route_from;
        this.route_to = route_to;
        this.price = price;
    }

    @Override
    public String toString() {
        return route_from + " - " + route_to;
    }

    public String getTime_queue() {
        return time_queue;
    }

    public void setTime_queue(String time_queue) {
        this.time_queue = time_queue;
    }

    public String getTime_depart() {
        return time_depart;
    }

    public void setTime_depart(String time_depart) {
        this.time_depart = time_depart;
    }

    public String getVan_company_uid() {
        return van_company_uid;
    }

    public void setVan_company_uid(String van_company_uid) {
        this.van_company_uid = van_company_uid;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
