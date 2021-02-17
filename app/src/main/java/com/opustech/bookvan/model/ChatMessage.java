package com.opustech.bookvan.model;

import com.google.firebase.Timestamp;

public class ChatMessage {
    String message, uid;
    Timestamp timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String message, String uid, Timestamp timestamp) {
        this.message = message;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
