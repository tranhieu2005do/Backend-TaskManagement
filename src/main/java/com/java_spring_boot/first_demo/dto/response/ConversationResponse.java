package com.java_spring_boot.first_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConversationResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("team_id")
    private Long teamId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("number_of_members")
    private Integer numberOfMembers;

    @JsonProperty("last_active")
    private LocalDateTime lastActive;

    @JsonProperty("last_sender")
    private String lastSender;

    @JsonProperty("last_message")
    private String lastMessage;

    @JsonProperty("last_updated")
    private  LocalDateTime lastUpdated;
}
