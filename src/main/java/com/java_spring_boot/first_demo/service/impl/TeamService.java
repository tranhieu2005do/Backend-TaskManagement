package com.java_spring_boot.first_demo.service.impl;

import com.java_spring_boot.first_demo.custom_annotation.Audit;
import com.java_spring_boot.first_demo.dto.request.CreatedConversationRequest;
import com.java_spring_boot.first_demo.dto.request.CreatedTeamRequest;
import com.java_spring_boot.first_demo.dto.response.*;
import com.java_spring_boot.first_demo.entity.*;
import com.java_spring_boot.first_demo.exception.NotFoundException;
import com.java_spring_boot.first_demo.repository.*;
import com.java_spring_boot.first_demo.service.interf.ITeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService implements ITeamService {

    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;
    private final NotificationRepository notificationRepository;
    private final ConversationService conversationService;


    @Audit(action = "CREATE_TEAM", entity="TEAM")
    @PreAuthorize("@authSecurity.isAuthenticated()")
    @Override
    public CreatedTeamResponse createTeam(Long creatorId, CreatedTeamRequest request) {
        log.info("Creating team with request {} by creator with id {}", request, creatorId);
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new NotFoundException("User with id " +  creatorId + " not found"));
        Team newTeam = Team.builder()
                .name(request.getTeamName())
                .description(request.getDescription())
                .createdBy(creator)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        teamRepository.save(newTeam);
        // creator is the first member
        TeamMember firstTeamMember = TeamMember.builder()
                .role(TeamMember.Role_Team.admin)
                .joinedAt(LocalDateTime.now())
                .team(newTeam)
                .user(creator)
                .active(true)
                .build();
        teamMemberRepository.save(firstTeamMember);
        log.info("Team with id {} has been created", newTeam.getId());
        conversationService.createConversation(CreatedConversationRequest.builder()
                        .name(request.getTeamName())
                        .teamId(newTeam.getId())
                        .type(Conversation.Type.GROUP)
                .build());
        conversationService.addToConversation(newTeam.getId(), creatorId);
        return CreatedTeamResponse.fromEntity(newTeam);
    }

    @Audit(action = "GET_TEAM_MEMBERS", entity="TeamMember")
    @PreAuthorize("@teamSecurity.isTeamMember(#teamId)")
    @Override
    public List<TeamMemberResponse> getTeamMembersByTeamId(Long teamId) {
        log.info("Getting team members of team which has id: {}", teamId);
        return teamMemberRepository.getTeamMembersByTeamId(teamId)
                .stream()
                .map(TeamMemberResponse::fromEntity)
                .toList();
    }

    @Audit(action = "GET_TEAM_TASKS", entity = "Task")
    @PreAuthorize("@teamSecurity.isTeamMember(#teamId)")
    @Override
    public List<TaskResponse> getTasksByTeamId(Long teamId) {
        return taskRepository.getTaskByTeamId(teamId)
                .stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    @Audit(action = "GET_ALL_TEAM_USER", entity = "TEAM")
    @PreAuthorize("@authSecurity.isCurrentUser(#userId)")
    @Override
    public PageResponse<TeamResponse> getAllTeamsOfUser(Long userId) {
        Pageable page = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        return PageResponse.fromPage(
                teamRepository.findAllTeamByUserId(userId, page)
                        .map(this::fromEntity)
        );
    }

    @Audit(action = "ADD_TEAM_MEMBER", entity="TeamMember")
    @PreAuthorize("@teamSecurity.isAdmin(#teamId)")
    @Override
    public AddTeamMemberToTeamResponse addTeamMemberToTeam(Long teamId, String email) {
        log.info("Adding team member with email {} to team with id {}", email, teamId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team with id " + teamId + " not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email " + email + " not found."));
//        if(user == null){
//            log.error("User with email {} not found", email);
//            throw new NotFoundException("User with email " + email + " not found");
//        }

        TeamMember newTeamMember = TeamMember.builder()
                .team(team)
                .user(user)
                .role(TeamMember.Role_Team.member)
                .joinedAt(LocalDateTime.now())
                .active(true)
                .build();
        teamMemberRepository.save(newTeamMember);

        team.setUpdatedAt(LocalDateTime.now());
        teamRepository.save(team);

        conversationService.addToConversation(teamId, user.getId());

        Notification newNotification = Notification.builder()
                .type(Notification.Type.newteam)
                .content("You have just been added to the team")
                .createdAt(LocalDateTime.now())
                .receiver(user)
                .build();
        notificationRepository.save(newNotification);

        log.info("Team member with email {} has been added to team with id {}", email, teamId);
        return AddTeamMemberToTeamResponse.fromTeamMember(newTeamMember);
    }

    @Audit(action="DELETE_TEAM_MEMBER", entity="TeamMember")
    @PreAuthorize("@teamSecurity.isAdmin(#teamId)")
    @Override
    public void kickTeamMember(Long teamId, String email) {
        log.info("Soft deleting team member with email {} from team with id {}", email, teamId);
        TeamMember teamMember = teamMemberRepository.getTeamMemberByTeamIdAndEmail(teamId, email);
        teamMember.setActive(false);
        teamMemberRepository.save(teamMember);
        log.info("Finished soft deleting team member with email {} from team with id {}", email, teamId);
    }

    private TeamResponse fromEntity(Team team) {
        return TeamResponse.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .description(team.getDescription())
                .numberOfMembers(teamMemberRepository.getNumberOfTeamMembersByTeamId(team.getId()))
                .numberOfTasks(taskRepository.getNumberOfTasksByTeamId(team.getId()))
                .createdBy(team.getCreatedBy().getEmail())
                .build();
    }
}
