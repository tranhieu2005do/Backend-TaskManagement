package com.java_spring_boot.first_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.java_spring_boot.first_demo.entity.Task;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public class TaskResponse {

    @JsonProperty("task_id")
    private Long taskId;

    @JsonProperty("created_by")
    private String creator;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private Task.Status status;

    @JsonProperty("due_date")
    private LocalDate dueDate;

    @JsonProperty("version")
    private Long version;

    public static TaskResponse fromEntity(Task task) {
        return TaskResponse.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .creator(task.getCreatedBy().getFullName())
                .version(task.getVersion())
                .build();
    }
}
