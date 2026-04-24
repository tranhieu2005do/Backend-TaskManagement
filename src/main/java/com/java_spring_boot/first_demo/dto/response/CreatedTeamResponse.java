package com.java_spring_boot.first_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java_spring_boot.first_demo.entity.Team;
import lombok.Builder;

@Builder
public class CreatedTeamResponse {

    @JsonProperty("team_id")
    private Long teamId;

    @JsonProperty("team_name")
    private String teamName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("created_by")
    private String creator;

    public static CreatedTeamResponse fromEntity(Team team){
        return CreatedTeamResponse.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .description(team.getDescription())
                .creator(team.getCreatedBy().getFullName())
                .build();
    }

}
