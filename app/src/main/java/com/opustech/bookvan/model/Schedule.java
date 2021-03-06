package com.opustech.bookvan.model;

public class Schedule {
    String time_pila, time_alis, van_company_uid, destination;

    public Schedule(String time_pila, String time_alis, String van_company_uid, String destination) {
        this.time_pila = time_pila;
        this.time_alis = time_alis;
        this.van_company_uid = van_company_uid;
        this.destination = destination;
    }

    public Schedule() {
    }

    public String getTime_pila() {
        return time_pila;
    }

    public void setTime_pila(String time_pila) {
        this.time_pila = time_pila;
    }

    public String getTime_alis() {
        return time_alis;
    }

    public void setTime_alis(String time_alis) {
        this.time_alis = time_alis;
    }

    public String getVan_company_uid() {
        return van_company_uid;
    }

    public void setVan_company_uid(String van_company_uid) {
        this.van_company_uid = van_company_uid;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
