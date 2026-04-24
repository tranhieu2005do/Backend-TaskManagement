package com.java_spring_boot.first_demo.service.interf;

import com.java_spring_boot.first_demo.dto.request.CreatedTeamRequest;
import com.java_spring_boot.first_demo.dto.response.*;

import java.util.List;

public interface ITeamService {

    CreatedTeamResponse createTeam(Long creatorId, CreatedTeamRequest request);

    List<TeamMemberResponse> getTeamMembersByTeamId(Long teamId);

    List<TaskResponse> getTasksByTeamId(Long teamId);

    PageResponse<TeamResponse> getAllTeamsOfUser(Long userId);

    AddTeamMemberToTeamResponse addTeamMemberToTeam(Long teamId, String email);

    void kickTeamMember(Long teamId, String email);
}
