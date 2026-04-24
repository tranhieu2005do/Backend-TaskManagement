package com.java_spring_boot.first_demo.service.socket_service.event;

import com.java_spring_boot.first_demo.entity.Notification;

public class NotificationCreatedEvent {

    private final Notification notification;

    public NotificationCreatedEvent(Notification notification) {
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }
}
