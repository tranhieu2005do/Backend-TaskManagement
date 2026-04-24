package com.java_spring_boot.first_demo.dto.request;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.java_spring_boot.first_demo.entity.Task;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdatedTaskRequest {

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("new_due_date")
    private LocalDate newDueDate;

    @JsonProperty("new_status")
    private Task.Status newStatus;

    @JsonProperty("version")
    private Long version;
}
