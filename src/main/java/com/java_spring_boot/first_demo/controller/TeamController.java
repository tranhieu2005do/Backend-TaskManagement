package com.java_spring_boot.first_demo.controller;

import com.java_spring_boot.first_demo.dto.request.CreatedTeamRequest;
import com.java_spring_boot.first_demo.dto.response.*;
import com.java_spring_boot.first_demo.entity.CustomUserDetail;
import com.java_spring_boot.first_demo.service.impl.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/team")
@RequiredArgsConstructor
@RestController
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreatedTeamResponse>> createTeam(@Valid @RequestBody CreatedTeamRequest request){
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long creatorId;
        if (principal instanceof CustomUserDetail) {
            creatorId = ((CustomUserDetail) principal).getId();
        } else {
            // Nếu principal là String (username), cần query database
            String username = (String) principal;
            // Hoặc throw exception nếu không mong đợi case này
            throw new RuntimeException("Invalid authentication principal type");
        }
        return ResponseEntity.ok(ApiResponse.<CreatedTeamResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Team created successfully")
                .data(teamService.createTeam(creatorId, request))
                .build());
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<ApiResponse<List<TeamMemberResponse>>> getTeamMembersByTeamId(@PathVariable Long teamId){
        return ResponseEntity.ok(ApiResponse.<List<TeamMemberResponse>>builder()
                .data(teamService.getTeamMembersByTeamId(teamId))
                .statusCode(HttpStatus.OK.value())
                .message("Team members found successfully")
                .build());
    }

    @GetMapping("/{teamId}/tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByTeamId(@PathVariable Long teamId){
        return ResponseEntity.ok(ApiResponse.<List<TaskResponse>>builder()
                .message("Team tasks found successfully")
                .statusCode(HttpStatus.OK.value())
                .data(teamService.getTasksByTeamId(teamId))
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TeamResponse>>> getAllTeamsOfUser(){
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = userDetail.getId();
        return ResponseEntity.ok(ApiResponse.<PageResponse<TeamResponse>>builder()
                .data(teamService.getAllTeamsOfUser(userId))
                .statusCode(HttpStatus.OK.value())
                .message("Teams found successfully")
                .build());
    }
}
