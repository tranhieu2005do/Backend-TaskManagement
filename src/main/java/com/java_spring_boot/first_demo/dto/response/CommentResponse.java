package com.java_spring_boot.first_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java_spring_boot.first_demo.entity.Comment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class CommentResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("commenter")
    private String commenter;

    @JsonProperty("content")
    private String content;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public static CommentResponse fromEntity(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .commenter(comment.getCommenter().getFullName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
