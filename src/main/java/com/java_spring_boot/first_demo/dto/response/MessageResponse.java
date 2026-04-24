package com.java_spring_boot.first_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java_spring_boot.first_demo.entity.Message;
import lombok.Builder;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("sender")
    private String senderName;

    @JsonProperty("content")
    private String content;

    @JsonProperty("type")
    private Message.Type type;

    @JsonProperty("url")
    private String fileUrl;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("file_size")
    private Long fileSize;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public static MessageResponse fromEntity(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .senderName(message.getSender().getFullName())
                .content(message.getContent())
                .type(message.getType())
                .fileUrl(message.getFileUrl())
                .fileName(message.getFileName())
                .fileSize(message.getFileSize())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
