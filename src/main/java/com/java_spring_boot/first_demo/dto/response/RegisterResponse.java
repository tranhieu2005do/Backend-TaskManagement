package com.java_spring_boot.first_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    @JsonProperty("name")
    private String fullName;

    @JsonProperty("mail")
    private String email;
}
