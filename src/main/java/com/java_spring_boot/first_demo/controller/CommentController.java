package com.java_spring_boot.first_demo.controller;

import com.java_spring_boot.first_demo.dto.request.CreatedCommentRequest;
import com.java_spring_boot.first_demo.dto.response.ApiResponse;
import com.java_spring_boot.first_demo.dto.response.CommentResponse;
import com.java_spring_boot.first_demo.dto.response.CreatedCommentResponse;
import com.java_spring_boot.first_demo.service.impl.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreatedCommentResponse>> createComment(
            @RequestBody CreatedCommentRequest createdCommentRequest) {
        return ResponseEntity.ok(ApiResponse.<CreatedCommentResponse>builder()
                .data(commentService.createComment(createdCommentRequest))
                .message("Successfully created comment")
                .statusCode(HttpStatus.CREATED.value())
                .build());
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getCommentsByTaskId(@PathVariable Long taskId) {
        return ResponseEntity.ok(ApiResponse.<List<CommentResponse>>builder()
                .data(commentService.getCommentsByTaskId(taskId))
                .message("Successfully getting comments")
                .statusCode(HttpStatus.OK.value())
                .build());
    }
    
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteComment(@RequestParam Long commentId) {
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .message("Successfully deleted comment")
                .build());
    }
}
