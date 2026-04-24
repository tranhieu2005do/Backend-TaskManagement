package com.java_spring_boot.first_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.java_spring_boot.first_demo.entity.TeamMember;
import lombok.Builder;

@Builder
public class TeamMemberResponse {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("role")
    private TeamMember.Role_Team role;

    public static TeamMemberResponse fromEntity(TeamMember teamMember) {
        return TeamMemberResponse.builder()
                .userId(teamMember.getUser().getId())
                .userName(teamMember.getUser().getEmail())
                .role(teamMember.getRole())
                .build();
    }
}
