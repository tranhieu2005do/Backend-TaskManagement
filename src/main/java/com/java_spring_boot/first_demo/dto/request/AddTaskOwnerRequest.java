package com.java_spring_boot.first_demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class AddTaskOwnerRequest {

    @JsonProperty("task_id")
    private Long taskId;

    @JsonProperty("mail_owner")
    private String email;
}
