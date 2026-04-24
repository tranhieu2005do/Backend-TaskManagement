package com.java_spring_boot.first_demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreatedTaskRequest {

    @JsonProperty("team_id")
    private Long teamId;
//    @JsonProperty("creator_id")
//    private Long creatorId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("due_date")
    private LocalDate dueDate;

    @JsonProperty("owner")
    private List<String> emailTaskOwner;
}
