package com.java_spring_boot.first_demo.service.impl;

import com.java_spring_boot.first_demo.custom_annotation.Audit;
import com.java_spring_boot.first_demo.dto.request.CreatedCommentRequest;
import com.java_spring_boot.first_demo.dto.response.CommentResponse;
import com.java_spring_boot.first_demo.dto.response.CreatedCommentResponse;
import com.java_spring_boot.first_demo.entity.Comment;
import com.java_spring_boot.first_demo.entity.Notification;
import com.java_spring_boot.first_demo.entity.Task;
import com.java_spring_boot.first_demo.entity.User;
import com.java_spring_boot.first_demo.exception.NotFoundException;
import com.java_spring_boot.first_demo.repository.CommentRepository;
import com.java_spring_boot.first_demo.repository.TaskRepository;
import com.java_spring_boot.first_demo.repository.UserRepository;
import com.java_spring_boot.first_demo.service.interf.ICommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService implements ICommentService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Audit(action = "SEND_COMMENT", entity = "Comment")
    @PreAuthorize("@commentSecurity.canSendComment(#request.taskId)")
    @Override
    @Transactional
    public CreatedCommentResponse createComment(CreatedCommentRequest request) {
        log.info("Creating comment with request {}", request);
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new NotFoundException("Task with id " + request.getTaskId() + " not found"));
        User commenter = userRepository.findById(request.getCommenterId())
                .orElseThrow(() -> new NotFoundException("User with id " + request.getCommenterId() + " not found"));

        Comment newComment = Comment.builder()
                .task(task)
                .commenter(commenter)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .content(request.getContent())
                .build();
        commentRepository.save(newComment);

        List<User> receivers = userRepository.findOwnerOfTask(task.getId());
        for(User receiver : receivers) {
            Notification notification = Notification.builder()
                    .receiver(receiver)
                    .content("Your task just have been commented")
                    .type(Notification.Type.newcomment)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        log.info("Created successfully comment by commenter {}", commenter.getFullName());
        applicationEventPublisher.publishEvent(newComment);
        return CreatedCommentResponse.builder()
                .commentContent(request.getContent())
                .commenter(commenter.getFullName())
                .taskName(task.getTitle())
                .build();
    }

    @PreAuthorize("@commentSecurity.canViewComment(#taskId)")
    @Override
    public List<CommentResponse> getCommentsByTaskId(Long taskId) {
        log.info("Getting comments of task with id {}", taskId);
        return commentRepository.getCommentsByTaskId(taskId)
                .stream()
                .map(CommentResponse::fromEntity)
                .toList();
    }

    @Audit(action = "DELETE_COMMENT", entity = "Comment")
    @PreAuthorize("@commentService.canDeleteComment(#commentId)")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteComment(Long commentId) {
        log.info("Deleting comment with id {}", commentId);
        commentRepository.deleteById(commentId);
    }
}
