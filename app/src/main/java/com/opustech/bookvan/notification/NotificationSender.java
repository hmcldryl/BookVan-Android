package com.opustech.bookvan.notification;

public class NotificationSender {
    public NotificationData notificationData;
    public String to;

    public NotificationSender(NotificationData notificationData, String to) {
        this.notificationData = notificationData;
        this.to = to;
    }

    public NotificationSender() {
    }
}
