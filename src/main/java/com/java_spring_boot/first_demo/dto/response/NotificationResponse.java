package com.java_spring_boot.first_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.java_spring_boot.first_demo.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data @AllArgsConstructor @NoArgsConstructor
public class NotificationResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("type")
    private Notification.Type type;

    @JsonProperty("content")
    private String content;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public static NotificationResponse fromEntity(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .type(notification.getType())
                .createdAt(notification.getCreatedAt())
                .build();
    }

}
