package com.java_spring_boot.first_demo.config.security;

import com.java_spring_boot.first_demo.repository.CommentRepository;
import com.java_spring_boot.first_demo.repository.DocumentRepository;
import com.java_spring_boot.first_demo.repository.TaskAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskSecurity {

    private final AuthSecurity auth;
    private final TaskAssignmentRepository  taskAssignmentRepository;
    private final CommentRepository commentRepository;
    private final DocumentRepository documentRepository;

    public boolean isTaskOwner(Long taskId){
        Long userId = auth.getCurrentUserId();
        return userId != null && taskAssignmentRepository.existsByTask_IdAndUser_Id(taskId,userId);
    }

    public boolean isTaskOwnerByDocumentId(Long documentId){
        Long userId = auth.getCurrentUserId();
        return userId != null
                && taskAssignmentRepository.existsByTask_IdAndUser_Id(
                        documentRepository.findTaskIdByDocumentId(documentId),
                        userId);
    }

}
