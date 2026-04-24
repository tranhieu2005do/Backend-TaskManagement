package com.java_spring_boot.first_demo.controller;

import com.java_spring_boot.first_demo.dto.response.AddTeamMemberToTeamResponse;
import com.java_spring_boot.first_demo.dto.response.ApiResponse;
import com.java_spring_boot.first_demo.service.impl.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/team-member")
public class TeamMemberController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<ApiResponse<AddTeamMemberToTeamResponse>> addTeamMemberToTeam(
            @RequestParam Long teamId,
            @RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.<AddTeamMemberToTeamResponse>builder()
                .message("Successfully added team member to team")
                .statusCode(HttpStatus.CREATED.value())
                .data(teamService.addTeamMemberToTeam(teamId, email))
                .build());
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> softDeleteMemberFromTeam(
            @RequestParam Long teamId,
            @RequestParam String email
    ){
        teamService.kickTeamMember(teamId, email);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Successfully deleted team member from team")
                .statusCode(HttpStatus.OK.value())
                .build());
    }
}
