package com.opustech.bookvan.model;

public class ChatConversation {
    String uid;

    public ChatConversation(String uid) {
        this.uid = uid;
    }

    public ChatConversation() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
