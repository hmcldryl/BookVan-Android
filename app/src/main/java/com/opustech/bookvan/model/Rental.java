package com.opustech.bookvan.model;

public class Rental {
    String uid,
            reference_number,
            rent_type,
            name,
            contact_number,
            pickup_location,
            pickup_date,
            pickup_time,
            destination,
            dropoff_location,
            dropoff_date,
            dropoff_time,
            status,
            timestamp;

    public Rental() {
    }

    public Rental(String uid, String reference_number, String rent_type, String name, String contact_number, String pickup_location, String pickup_date, String pickup_time, String destination, String dropoff_location, String dropoff_date, String dropoff_time, String status, String timestamp) {
        this.uid = uid;
        this.reference_number = reference_number;
        this.rent_type = rent_type;
        this.name = name;
        this.contact_number = contact_number;
        this.pickup_location = pickup_location;
        this.pickup_date = pickup_date;
        this.pickup_time = pickup_time;
        this.destination = destination;
        this.dropoff_location = dropoff_location;
        this.dropoff_date = dropoff_date;
        this.dropoff_time = dropoff_time;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReference_number() {
        return reference_number;
    }

    public void setReference_number(String reference_number) {
        this.reference_number = reference_number;
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

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getPickup_location() {
        return pickup_location;
    }

    public void setPickup_location(String pickup_location) {
        this.pickup_location = pickup_location;
    }

    public String getPickup_date() {
        return pickup_date;
    }

    public void setPickup_date(String pickup_date) {
        this.pickup_date = pickup_date;
    }

    public String getPickup_time() {
        return pickup_time;
    }

    public void setPickup_time(String pickup_time) {
        this.pickup_time = pickup_time;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDropoff_location() {
        return dropoff_location;
    }

    public void setDropoff_location(String dropoff_location) {
        this.dropoff_location = dropoff_location;
    }

    public String getDropoff_date() {
        return dropoff_date;
    }

    public void setDropoff_date(String dropoff_date) {
        this.dropoff_date = dropoff_date;
    }

    public String getDropoff_time() {
        return dropoff_time;
    }

    public void setDropoff_time(String dropoff_time) {
        this.dropoff_time = dropoff_time;
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
}
