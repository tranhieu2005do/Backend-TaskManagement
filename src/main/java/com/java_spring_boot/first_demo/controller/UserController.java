package com.java_spring_boot.first_demo.controller;

import com.java_spring_boot.first_demo.dto.request.ChangePasswordRequest;
import com.java_spring_boot.first_demo.dto.response.ApiResponse;
import com.java_spring_boot.first_demo.entity.CustomUserDetail;
import com.java_spring_boot.first_demo.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> changeInformation(
            @RequestParam(required = false) String newUsername,
            @RequestBody(required = false) ChangePasswordRequest changePasswordRequest
            ){
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = userDetail.getId();

        if(newUsername != null){
            userService.changeUserName(newUsername, userId);
        }
        if(changePasswordRequest != null){
            userService.changePassword(changePasswordRequest, userId);
        }
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Success")
                .build());
    }
}
