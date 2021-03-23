package com.opustech.bookvan.model;

public class Rent {
    String uid, rent_type, name, location_pickup, location_dropoff, contact_number, destination, schedule_start_date, schedule_end_date, timestamp;

    public Rent() {
    }

    public Rent(String uid, String rent_type, String name, String location_pickup, String location_dropoff, String contact_number, String destination, String schedule_start_date, String schedule_end_date, String timestamp) {
        this.uid = uid;
        this.rent_type = rent_type;
        this.name = name;
        this.location_pickup = location_pickup;
        this.location_dropoff = location_dropoff;
        this.contact_number = contact_number;
        this.destination = destination;
        this.schedule_start_date = schedule_start_date;
        this.schedule_end_date = schedule_end_date;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRent_type() {
        return rent_type;
    }

    public void setRent_type(String rent_type) {
        this.rent_type = rent_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation_pickup() {
        return location_pickup;
    }

    public void setLocation_pickup(String location_pickup) {
        this.location_pickup = location_pickup;
    }

    public String getLocation_dropoff() {
        return location_dropoff;
    }

    public void setLocation_dropoff(String location_dropoff) {
        this.location_dropoff = location_dropoff;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSchedule_start_date() {
        return schedule_start_date;
    }

    public void setSchedule_start_date(String schedule_start_date) {
        this.schedule_start_date = schedule_start_date;
    }

    public String getSchedule_end_date() {
        return schedule_end_date;
    }

    public void setSchedule_end_date(String schedule_end_date) {
        this.schedule_end_date = schedule_end_date;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
