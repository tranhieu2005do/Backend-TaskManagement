package com.java_spring_boot.first_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java_spring_boot.first_demo.entity.Conversation;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatedConversationResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private Conversation.Type type;

    public static CreatedConversationResponse fromEntity(Conversation conversation){
        return CreatedConversationResponse.builder()
                .id(conversation.getId())
                .name(conversation.getName())
                .type(conversation.getType())
                .build();
    }
}
