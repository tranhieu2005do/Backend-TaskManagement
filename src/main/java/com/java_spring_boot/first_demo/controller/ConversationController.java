package com.java_spring_boot.first_demo.controller;

import com.java_spring_boot.first_demo.dto.response.ApiResponse;
import com.java_spring_boot.first_demo.dto.response.ConversationResponse;
import com.java_spring_boot.first_demo.service.impl.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversation")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getAllConversations(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.<List<ConversationResponse>>builder()
                .data(conversationService.getAllConversationsOfUser(userId))
                .message("Get conversation of user successfully")
                .statusCode(HttpStatus.OK.value())
                .build());
    }
}
