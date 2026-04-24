package com.java_spring_boot.first_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java_spring_boot.first_demo.entity.Team;
import lombok.Builder;

@Builder
public class TeamResponse {

    @JsonProperty("team_id")
    private Long teamId;

    @JsonProperty("team_name")
    private String teamName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("number_of_members")
    private Integer numberOfMembers;

    @JsonProperty("number_of_tasks")
    private Integer numberOfTasks;

    @JsonProperty("created_by")
    private String createdBy;

    public static TeamResponse fromEntity(Team team){
        return TeamResponse.builder()
                .teamName(team.getName())
                .description(team.getDescription())
                .build();
    }
}
