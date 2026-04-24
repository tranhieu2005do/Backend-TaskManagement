package com.java_spring_boot.first_demo.service.impl;

import com.java_spring_boot.first_demo.custom_annotation.Audit;
import com.java_spring_boot.first_demo.dto.response.DocumentResponse;
import com.java_spring_boot.first_demo.entity.Document;
import com.java_spring_boot.first_demo.entity.Task;
import com.java_spring_boot.first_demo.exception.NotFoundException;
import com.java_spring_boot.first_demo.repository.DocumentRepository;
import com.java_spring_boot.first_demo.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.Acceleration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public DocumentResponse createDocument(Long taskId){
        log.info("Creating document for task with id {}", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found"));
        Document newDocument = Document.builder()
                .task(task)
                .title(task.getTitle())
                .content(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return DocumentResponse.of(newDocument);
    }

    @Audit(action = "GET_DOCUMENT", entity = "Document")
    @PreAuthorize("@documentSecurity.canViewDocument(#taskId)")
    @Transactional(readOnly = true, rollbackFor = AccessDeniedException.class)
    public DocumentResponse getDocumentByTaskId(Long taskId){
        log.info("Getting the document with id {}", taskId);
        return DocumentResponse.of(
                documentRepository.findByTaskId(taskId));
    }

    @Audit(action = "EDIT_DOCUMENT", entity = "Document")
    @PreAuthorize("@documentSecurity.canEditDocument(#documentId)")
    @Transactional
    public void updateDocument(byte[] content, Long documentId){
        log.info("Updating the document with new content");
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Not found document with id: " + documentId));
        document.setContent(content);
        document.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(document);
    }
}
