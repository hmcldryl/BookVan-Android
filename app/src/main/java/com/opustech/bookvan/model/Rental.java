package com.opustech.bookvan.model;

public class Rental {
    String uid,
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
            timestamp;

    public Rental() {
    }

    public Rental(String uid, String rent_type, String name, String contact_number, String pickup_location, String pickup_date, String pickup_time, String destination, String dropoff_location, String dropoff_date, String dropoff_time, String timestamp) {
        this.uid = uid;
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
        this.timestamp = timestamp;
    }


}
