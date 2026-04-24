package com.java_spring_boot.first_demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @JsonProperty("email")
    private String email;

    @JsonProperty("reset_token")
    private String resetToken;

    @JsonProperty("new_password")
    private String newPassword;
}
