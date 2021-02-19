package com.opustech.bookvan.model;

public class ChatConversation {
    String uid, timestamp;

    public ChatConversation(String uid, String timestamp) {
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public ChatConversation() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
