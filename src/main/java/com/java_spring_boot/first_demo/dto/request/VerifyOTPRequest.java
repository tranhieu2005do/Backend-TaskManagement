package com.java_spring_boot.first_demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VerifyOTPRequest {

    @JsonProperty("email")
    private String email;

    @JsonProperty("otp")
    private String otp;
}
