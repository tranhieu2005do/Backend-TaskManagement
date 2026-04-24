package com.java_spring_boot.first_demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java_spring_boot.first_demo.entity.Notification;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatedNotificationRequest {

    @JsonProperty("receiver_id")
    private Long receiverId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("type")
    private Notification.Type type;
}
