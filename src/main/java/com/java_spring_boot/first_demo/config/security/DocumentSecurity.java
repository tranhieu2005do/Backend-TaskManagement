package com.java_spring_boot.first_demo.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentSecurity {

    private final TaskSecurity taskSecurity;
    private final TeamSecurity teamSecurity;

    public boolean canEditDocument(Long documentId){
        return taskSecurity.isTaskOwnerByDocumentId(documentId)
                || teamSecurity.isAdminByDocumentId(documentId);
    }

    public boolean canViewDocument(Long taskId){
        return teamSecurity.isTeamMemberByTaskId(taskId);
    }
}
