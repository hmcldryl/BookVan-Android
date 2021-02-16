package com.opustech.bookvan.model;

public class ChatMessage {
    String message, timestamp, uid;

    public ChatMessage() {
    }

    public ChatMessage(String message, String timestamp, String uid) {
        this.message = message;
        this.timestamp = timestamp;
        this.uid = uid;
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
