package com.java_spring_boot.first_demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java_spring_boot.first_demo.entity.Conversation;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatedConversationRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private Conversation.Type type;

    @JsonProperty("team_id")
    private Long teamId;
}
