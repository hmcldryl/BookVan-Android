package com.opustech.bookvan.model;

public class RentChatMessage {
    String uid,
            message,
            type,
            timestamp;

    public RentChatMessage() {
    }

    public RentChatMessage(String uid, String message, String type, String timestamp) {
        this.uid = uid;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
