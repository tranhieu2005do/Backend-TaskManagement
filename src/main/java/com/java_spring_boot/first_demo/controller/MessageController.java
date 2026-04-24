package com.java_spring_boot.first_demo.controller;

import com.java_spring_boot.first_demo.dto.response.ApiResponse;
import com.java_spring_boot.first_demo.dto.response.MessageResponse;
import com.java_spring_boot.first_demo.entity.CustomUserDetail;
import com.java_spring_boot.first_demo.service.impl.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @RequestParam String content,
            @RequestParam Long conversationId,
            @RequestParam(required = false) MultipartFile file
            ) throws IOException {
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        Long senderId = userDetail.getId();
        return ResponseEntity.ok(ApiResponse.<MessageResponse>builder()
                .message("Send message successfully!")
                .statusCode(HttpStatus.CREATED.value())
                .data(messageService.sendMessage(senderId, content, conversationId, file))
                .build());
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessagesInConversation(
            @PathVariable Long conversationId,
            @RequestParam(required = false) Long cursorId){
        return ResponseEntity.ok(ApiResponse.<List<MessageResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get Messages Successfully!")
                .data(messageService.getMessagesInConversation(conversationId, cursorId))
                .build());
    }
}
