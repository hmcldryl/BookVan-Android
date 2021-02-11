package com.opustech.bookvan.model;

public class ChatMessage {
    String name, photo_url, message, timestamp, uid;

    public ChatMessage(String name, String photo_url, String message, String timestamp, String uid) {
        this.name = name;
        this.photo_url = photo_url;
        this.message = message;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public ChatMessage() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
