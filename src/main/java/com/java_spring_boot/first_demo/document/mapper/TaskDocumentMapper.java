package com.java_spring_boot.first_demo.document.mapper;

import com.java_spring_boot.first_demo.document.TaskDocument;
import com.java_spring_boot.first_demo.entity.Task;
import com.java_spring_boot.first_demo.repository.TaskAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskDocumentMapper {

    private final TaskAssignmentRepository taskAssignmentRepository;

    public TaskDocument mapToTaskDocument(Task task){
        List<Long> participantIds = taskAssignmentRepository.findOwnerIdsByTaskId(task.getId());

        return TaskDocument.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())

                .participantIds(participantIds)

                .createdById(task.getCreatedBy().getId())
                .createdByName(task.getCreatedBy().getFullName())

                .teamId(task.getTeam().getId())
                .teamName(task.getTeam().getName())

                .status(task.getStatus().name())
                .dueDate(task.getDueDate())
                .build();
    }
}
