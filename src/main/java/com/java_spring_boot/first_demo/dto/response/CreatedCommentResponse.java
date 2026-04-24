package com.java_spring_boot.first_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class CreatedCommentResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("task_name")
    private String taskName;

    @JsonProperty("comment_content")
    private String commentContent;

    @JsonProperty("commenter")
    private String commenter;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

}