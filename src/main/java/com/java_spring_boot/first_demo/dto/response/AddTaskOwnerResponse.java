package com.java_spring_boot.first_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public class AddTaskOwnerResponse {

    @JsonProperty("new_owner")
    private String newOwner;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;
}
