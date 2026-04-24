package com.java_spring_boot.first_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.java_spring_boot.first_demo.entity.Task;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UnfinishedTask {

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("team_name")
    private String teamName;

    @JsonProperty("due_date")
    private LocalDate dueDate;

    public static UnfinishedTask fromEntity(Task task){
        return UnfinishedTask.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .teamName(task.getTeam().getName())
                .dueDate(task.getDueDate())
                .build();
    }
}
