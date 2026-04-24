package com.java_spring_boot.first_demo.service.socket_service.event;

import com.java_spring_boot.first_demo.entity.Message;

public class MessageSentEvent {

    private final Message message;

    public MessageSentEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

}
