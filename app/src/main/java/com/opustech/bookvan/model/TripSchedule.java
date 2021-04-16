package com.opustech.bookvan.model;

import androidx.annotation.NonNull;

public class TripSchedule {
    String time_queue, time_depart;

    public TripSchedule() {
    }

    public TripSchedule(String time_depart) {
        this.time_depart = time_depart;
    }

    public TripSchedule(String time_queue, String time_depart) {
        this.time_queue = time_queue;
        this.time_depart = time_depart;
    }

    @NonNull
    @Override
    public String toString() {
        return time_depart;
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

}
