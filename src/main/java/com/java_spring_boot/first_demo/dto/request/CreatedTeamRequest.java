package com.java_spring_boot.first_demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreatedTeamRequest {

    @JsonProperty("name")
    private String teamName;

    @JsonProperty("description")
    private String description;
}
