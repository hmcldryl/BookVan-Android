package com.opustech.bookvan.model;

public class Booking {
    String reference_number,
            uid,
            name,
            contact_number,
            route_from,
            route_to,
            schedule_date,
            schedule_time,
            transport_uid,
            driver_name,
            van_number,
            status,
            remarks,
            timestamp_date,
            timestamp;
    int count_adult, count_child, count_special;
    double price, commission;

    public Booking() {
    }

    public Booking(String reference_number, String uid, String name, String contact_number, String route_from, String route_to, String schedule_date, String schedule_time, String transport_uid, String status, String timestamp_date, String timestamp, int count_adult, int count_child, int count_special, double price, double commission) {
        this.reference_number = reference_number;
        this.uid = uid;
        this.name = name;
        this.contact_number = contact_number;
        this.route_from = route_from;
        this.route_to = route_to;
        this.schedule_date = schedule_date;
        this.schedule_time = schedule_time;
        this.transport_uid = transport_uid;
        this.status = status;
        this.timestamp_date = timestamp_date;
        this.timestamp = timestamp;
        this.count_adult = count_adult;
        this.count_child = count_child;
        this.count_special = count_special;
        this.price = price;
        this.commission = commission;
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

    public String getVan_number() {
        return van_number;
    }

    public void setVan_number(String van_number) {
        this.van_number = van_number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTimestamp_date() {
        return timestamp_date;
    }

    public void setTimestamp_date(String timestamp_date) {
        this.timestamp_date = timestamp_date;
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

    public int getCount_special() {
        return count_special;
    }

    public void setCount_special(int count_special) {
        this.count_special = count_special;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }
}
