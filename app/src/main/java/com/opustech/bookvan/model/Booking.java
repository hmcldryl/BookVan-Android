package com.opustech.bookvan.model;

public class Booking {
    String customer_uid,
            customer_name,
            customer_email,
            booking_contact_number,
            booking_location_from,
            booking_location_to,
            booking_schedule_date,
            booking_schedule_time,
            booking_count_adult,
            booking_count_child,
            booking_status;

    public Booking(String customer_uid, String customer_name, String customer_email, String booking_contact_number, String booking_location_from, String booking_location_to, String booking_schedule_date, String booking_schedule_time, String booking_count_adult, String booking_count_child, String booking_status) {
        this.customer_uid = customer_uid;
        this.customer_name = customer_name;
        this.customer_email = customer_email;
        this.booking_contact_number = booking_contact_number;
        this.booking_location_from = booking_location_from;
        this.booking_location_to = booking_location_to;
        this.booking_schedule_date = booking_schedule_date;
        this.booking_schedule_time = booking_schedule_time;
        this.booking_count_adult = booking_count_adult;
        this.booking_count_child = booking_count_child;
        this.booking_status = booking_status;
    }

    public String getCustomer_uid() {
        return customer_uid;
    }

    public void setCustomer_uid(String customer_uid) {
        this.customer_uid = customer_uid;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getBooking_contact_number() {
        return booking_contact_number;
    }

    public void setBooking_contact_number(String booking_contact_number) {
        this.booking_contact_number = booking_contact_number;
    }

    public String getBooking_location_from() {
        return booking_location_from;
    }

    public void setBooking_location_from(String booking_location_from) {
        this.booking_location_from = booking_location_from;
    }

    public String getBooking_location_to() {
        return booking_location_to;
    }

    public void setBooking_location_to(String booking_location_to) {
        this.booking_location_to = booking_location_to;
    }

    public String getBooking_schedule_date() {
        return booking_schedule_date;
    }

    public void setBooking_schedule_date(String booking_schedule_date) {
        this.booking_schedule_date = booking_schedule_date;
    }

    public String getBooking_schedule_time() {
        return booking_schedule_time;
    }

    public void setBooking_schedule_time(String booking_schedule_time) {
        this.booking_schedule_time = booking_schedule_time;
    }

    public String getBooking_count_adult() {
        return booking_count_adult;
    }

    public void setBooking_count_adult(String booking_count_adult) {
        this.booking_count_adult = booking_count_adult;
    }

    public String getBooking_count_child() {
        return booking_count_child;
    }

    public void setBooking_count_child(String booking_count_child) {
        this.booking_count_child = booking_count_child;
    }

    public String getBooking_status() {
        return booking_status;
    }

    public void setBooking_status(String booking_status) {
        this.booking_status = booking_status;
    }
}
