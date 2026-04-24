package com.java_spring_boot.first_demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ForgotPasswordRequest {

    @JsonProperty("email")
    private String email;
}
