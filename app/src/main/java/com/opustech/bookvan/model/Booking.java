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
            transport_uid,
            driver_name,
            plate_number,
            status,
            timestamp;
    int count_adult, count_child;
    float price;

    public Booking() {
    }

    public Booking(String reference_number, String uid, String name, String contact_number, String location_from, String location_to, String schedule_date, String schedule_time, String transport_uid, String driver_name, String plate_number, String status, String timestamp, int count_adult, int count_child, float price) {
        this.reference_number = reference_number;
        this.uid = uid;
        this.name = name;
        this.contact_number = contact_number;
        this.location_from = location_from;
        this.location_to = location_to;
        this.schedule_date = schedule_date;
        this.schedule_time = schedule_time;
        this.transport_uid = transport_uid;
        this.driver_name = driver_name;
        this.plate_number = plate_number;
        this.status = status;
        this.timestamp = timestamp;
        this.count_adult = count_adult;
        this.count_child = count_child;
        this.price = price;
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

    public String getTransport_uid() {
        return transport_uid;
    }

    public void setTransport_uid(String transport_uid) {
        this.transport_uid = transport_uid;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getPlate_number() {
        return plate_number;
    }

    public void setPlate_number(String plate_number) {
        this.plate_number = plate_number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getCount_adult() {
        return count_adult;
    }

    public void setCount_adult(int count_adult) {
        this.count_adult = count_adult;
    }

    public int getCount_child() {
        return count_child;
    }

    public void setCount_child(int count_child) {
        this.count_child = count_child;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
