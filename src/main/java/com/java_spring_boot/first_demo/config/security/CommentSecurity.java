package com.java_spring_boot.first_demo.config.security;

import com.java_spring_boot.first_demo.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentSecurity {

    private final AuthSecurity authSecurity;
    private final TeamSecurity teamSecurity;
    private final TaskSecurity taskSecurity;
    private final CommentRepository commentRepository;

    public boolean isCommenter(Long commentId){
        Long userId = authSecurity.getCurrentUserId();
        return userId != null
                && commentRepository.existsByIdAndCommenter_Id(commentId, userId);
    }

    public boolean canDeleteComment(Long commentId){
        return isCommenter(commentId)
                || teamSecurity.isAdminByCommentId(commentId);
    }

    public boolean canSendComment(Long taskId){
        return taskSecurity.isTaskOwner(taskId)
                || teamSecurity.isAdminByTaskId(taskId);
    }

    public boolean canViewComment(Long taskId){
        return teamSecurity.isTeamMemberByTaskId(taskId);
    }
}
