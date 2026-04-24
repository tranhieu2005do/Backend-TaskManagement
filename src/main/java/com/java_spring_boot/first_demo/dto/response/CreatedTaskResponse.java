package com.java_spring_boot.first_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java_spring_boot.first_demo.entity.Task;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public class CreatedTaskResponse {

    @JsonProperty("task_id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("created_by")
    private String creator;

    @JsonProperty("status")
    private Task.Status status;

    @JsonProperty("due_date")
    private LocalDateTime dueDate;
}
