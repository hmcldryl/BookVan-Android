package com.opustech.bookvan.model;

public class Booking {
    String reference_number,
            uid,
            name,
            contact_number,
            location_from,
            location_to,
            schedule_date,
            schedule_time,
            count_adult,
            count_child,
            price,
            timestamp,
            status;

    public Booking() {
    }

    public Booking(String reference_number, String uid, String name, String contact_number, String location_from, String location_to, String schedule_date, String schedule_time, String count_adult, String count_child, String price, String timestamp, String status) {
        this.reference_number = reference_number;
        this.uid = uid;
        this.name = name;
        this.contact_number = contact_number;
        this.location_from = location_from;
        this.location_to = location_to;
        this.schedule_date = schedule_date;
        this.schedule_time = schedule_time;
        this.count_adult = count_adult;
        this.count_child = count_child;
        this.price = price;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getReference_number() {
        return reference_number;
    }

    public void setReference_number(String reference_number) {
        this.reference_number = reference_number;
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

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getLocation_from() {
        return location_from;
    }

    public void setLocation_from(String location_from) {
        this.location_from = location_from;
    }

    public String getLocation_to() {
        return location_to;
    }

    public void setLocation_to(String location_to) {
        this.location_to = location_to;
    }

    public String getSchedule_date() {
        return schedule_date;
    }

    public void setSchedule_date(String schedule_date) {
        this.schedule_date = schedule_date;
    }

    public String getSchedule_time() {
        return schedule_time;
    }

    public void setSchedule_time(String schedule_time) {
        this.schedule_time = schedule_time;
    }

    public String getCount_adult() {
        return count_adult;
    }

    public void setCount_adult(String count_adult) {
        this.count_adult = count_adult;
    }

    public String getCount_child() {
        return count_child;
    }

    public void setCount_child(String count_child) {
        this.count_child = count_child;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
