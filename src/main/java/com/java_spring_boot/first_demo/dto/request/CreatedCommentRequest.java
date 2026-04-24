package com.java_spring_boot.first_demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreatedCommentRequest {

    @JsonProperty("task_id")
    private Long taskId;

    @JsonProperty("commenter_id")
    private Long commenterId;

    @JsonProperty("content")
    private String content;
}
