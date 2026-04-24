package com.java_spring_boot.first_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.java_spring_boot.first_demo.entity.TeamMember;
import lombok.Builder;

@Builder
public class AddTeamMemberToTeamResponse {

    @JsonProperty("team_id")
    private Long teamId;

    @JsonProperty("team_name")
    private String teamName;

    @JsonProperty("new_member_name")
    private String newMemberName;

    @JsonProperty("new_member_id")
    private Long newMemberId;

    @JsonProperty("role")
    private TeamMember.Role_Team role;

    public static AddTeamMemberToTeamResponse fromTeamMember(TeamMember teamMember) {
        return AddTeamMemberToTeamResponse.builder()
                .teamId(teamMember.getTeam().getId())
                .teamName(teamMember.getTeam().getName())
                .newMemberId(teamMember.getUser().getId())
                .newMemberName(teamMember.getUser().getFullName())
                .role(teamMember.getRole())
                .build();
    }
}
