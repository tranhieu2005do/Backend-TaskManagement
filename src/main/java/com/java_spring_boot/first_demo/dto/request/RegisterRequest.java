package com.java_spring_boot.first_demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterRequest {

    @JsonProperty("name")
    private String fullName;

    @JsonProperty("mail")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("confirm_pass")
    private String confirmPassword;

}
