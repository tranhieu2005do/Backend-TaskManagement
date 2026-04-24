package com.java_spring_boot.first_demo.controller;

import com.java_spring_boot.first_demo.config.security.TaskSecurity;
import com.java_spring_boot.first_demo.config.security.TeamSecurity;
import com.java_spring_boot.first_demo.document.TaskDocument;
import com.java_spring_boot.first_demo.dto.request.AddTaskOwnerRequest;
import com.java_spring_boot.first_demo.dto.request.CreatedTaskRequest;
import com.java_spring_boot.first_demo.dto.request.UpdatedTaskRequest;
import com.java_spring_boot.first_demo.dto.response.*;
import com.java_spring_boot.first_demo.entity.CustomUserDetail;
import com.java_spring_boot.first_demo.entity.Task;
import com.java_spring_boot.first_demo.repository.TaskRepository;
import com.java_spring_boot.first_demo.service.impl.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/task")
@Slf4j
public class TaskController {

    private final TaskService taskService;
    private final TeamSecurity  teamSecurity;

    @PostMapping
    public ResponseEntity<ApiResponse<CreatedTaskResponse>> createTask(
            @Valid @RequestBody CreatedTaskRequest request){
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        Long creatorId = userDetail.getId();
        return ResponseEntity.ok(ApiResponse.<CreatedTaskResponse>builder()
                .data(taskService.createTask(creatorId, request))
                .statusCode(HttpStatus.OK.value())
                .message("Task created successfully")
                .build());
    }

    @PostMapping("/owner-adding")
    public ResponseEntity<ApiResponse<AddTaskOwnerResponse>> addTaskOwner(
            @Valid @RequestBody AddTaskOwnerRequest request){
        return ResponseEntity.ok(ApiResponse.<AddTaskOwnerResponse>builder()
                .message("Task added successfully")
                .statusCode(HttpStatus.CREATED.value())
                .data(taskService.addTaskOwner(request))
                .build());
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> updateDueDate(
            @PathVariable Long taskId,
            @RequestBody UpdatedTaskRequest request) {
        taskService.updateTask(taskId, request.getNewDueDate(), request.getNewStatus(), request.getVersion());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Task updated successfully")
                .build());
    }

    @GetMapping("/unfinished")
    public ResponseEntity<ApiResponse<PageResponse<UnfinishedTask>>> getUnfinishedTasksOfUser(){
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = userDetail.getId();
        return ResponseEntity.ok(ApiResponse.<PageResponse<UnfinishedTask>>builder()
                .message("Get tasks unfinished successfully")
                .statusCode(HttpStatus.OK.value())
                .data(taskService.getAllUnfinishedTasksOfUser(userId))
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TaskResponse>>> filterTasksOfUser(
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) Task.Status status,
            @RequestParam(required = false) LocalDate first,
            @RequestParam(required = false) LocalDate second,
            @RequestParam(required = false) String sortBy) {
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = userDetail.getId();
        return ResponseEntity.ok(ApiResponse.<PageResponse<TaskResponse>>builder()
                .data(taskService.filterTasksOfUser(userId, teamId, status, first, second,sortBy))
                .statusCode(HttpStatus.OK.value())
                .message("Get tasks successfully")
                .build());
    }

    @GetMapping("/elastic-search")
    public ResponseEntity<ApiResponse<List<TaskDocument>>> searchTasks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ){
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = userDetail.getId();
        return ResponseEntity.ok(ApiResponse.<List<TaskDocument>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get tasks successfully")
                .data(taskService.searchAdvanced(keyword, status, userId))
                .build());
    }
}
