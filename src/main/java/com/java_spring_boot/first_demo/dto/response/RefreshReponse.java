package com.java_spring_boot.first_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class RefreshReponse {

    @JsonProperty("access_token")
    private String accessToken;
}
