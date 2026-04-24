package com.java_spring_boot.first_demo.controller;

import com.java_spring_boot.first_demo.dto.response.ApiResponse;
import com.java_spring_boot.first_demo.dto.response.NotificationResponse;
import com.java_spring_boot.first_demo.dto.response.PageResponse;
import com.java_spring_boot.first_demo.entity.CustomUserDetail;
import com.java_spring_boot.first_demo.service.impl.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getAllNotifications() {
        Long userId = getAuthenticatedUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .message("User not authenticated")
                            .build());
        }

        return ResponseEntity.ok(ApiResponse.<PageResponse<NotificationResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .data(notificationService.getAllNotifications(userId))
                .message("Notifications found")
                .build());
    }

    @PatchMapping("/read-all")
    public ResponseEntity<?> markReadAllUnReadNotifications() {
        Long userId = getAuthenticatedUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .message("User not authenticated")
                            .build());
        }

        notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Notifications marked as read")
                .build());
    }

    @PatchMapping("/{notificationId}/unread")
    public ResponseEntity<ApiResponse<Void>> markUnreadNotification(
            @PathVariable Long notificationId) {
        notificationService.markUnreadNotification(notificationId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Notification has been marked as unread")
                .build());
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markReadNotification(
            @PathVariable Long notificationId) {
        notificationService.markReadNotification(notificationId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Notification has been marked as unread")
                .build());
    }

    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCountNotifications(){
        Long userId = getAuthenticatedUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .message("User not authenticated")
                            .build());
        }

        return ResponseEntity.ok(ApiResponse.<Integer>builder()
                .data(notificationService.getUnreadCountNotifications(userId))
                .message("Unread Notifications found")
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetail) {
            return ((CustomUserDetail) principal).getId();
        } else if (principal instanceof OAuth2User) {
            // If using standard OAuth2, extract email to find user ID or similar
            // For now, return null as Google login is handled manually via JWT
            return null;
        }
        return null;
    }
}
