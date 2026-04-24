package com.java_spring_boot.first_demo.config.security;

import com.java_spring_boot.first_demo.entity.TeamMember;
import com.java_spring_boot.first_demo.repository.CommentRepository;
import com.java_spring_boot.first_demo.repository.DocumentRepository;
import com.java_spring_boot.first_demo.repository.TaskRepository;
import com.java_spring_boot.first_demo.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TeamSecurity {

    private final TeamMemberRepository teamMemberRepository;
    private final TaskRepository  taskRepository;
    private final DocumentRepository documentRepository;
    private final CommentRepository commentRepository;
    private final AuthSecurity authSecurity;


    public boolean isTeamMember(Long teamId){
        Long userId = authSecurity.getCurrentUserId();
        return userId != null && teamMemberRepository.existsByUserIdAndTeamIdAndActiveTrue(userId, teamId);
    }

    public boolean isTeamMemberByTaskId(Long taskId){
        Long userId = authSecurity.getCurrentUserId();
        return userId != null
                && teamMemberRepository.existsByUserIdAndTeamIdAndActiveTrue(
                        userId,
                taskRepository.findTeamIdByTaskId(taskId));
    }


    public boolean isAdmin(Long teamId){
        Long userId = authSecurity.getCurrentUserId();
        return userId != null && teamMemberRepository
                .existsByUser_IdAndTeam_IdAndRoleAndActiveTrue(
                        userId,
                        teamId,
                        TeamMember.Role_Team.admin
                );
    }

    public boolean isAdminByTaskId(Long taskId){
        Long userId = authSecurity.getCurrentUserId();
        log.info("Checking admin: userId={} taskId={} teamId={}", userId, taskId, taskRepository.findTeamIdByTaskId(taskId));
        return userId != null && teamMemberRepository.existsByUser_IdAndTeam_IdAndRoleAndActiveTrue(
                userId,
                taskRepository.findTeamIdByTaskId(taskId),
                TeamMember.Role_Team.admin
        );
    }

    public boolean isAdminByDocumentId(Long documentId){
        Long userId = authSecurity.getCurrentUserId();
        return userId != null
                && teamMemberRepository
                .existsByUser_IdAndTeam_IdAndRoleAndActiveTrue(
                        userId,
                        documentRepository.findTeamIdByDocumentId(documentId),
                        TeamMember.Role_Team.admin
                );
    }

    public boolean isAdminByCommentId(Long commentId){
        Long userId = authSecurity.getCurrentUserId();
        return userId != null
                && teamMemberRepository.existsByUser_IdAndTeam_IdAndRoleAndActiveTrue(
                        userId,
                        commentRepository.findTeamIdByCommentId(commentId),
                        TeamMember.Role_Team.admin
        );
    }

}
