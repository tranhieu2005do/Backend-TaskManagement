package com.java_spring_boot.first_demo.service.interf;

import com.java_spring_boot.first_demo.dto.request.AddTaskOwnerRequest;
import com.java_spring_boot.first_demo.dto.request.CreatedTaskRequest;
import com.java_spring_boot.first_demo.dto.response.*;
import com.java_spring_boot.first_demo.entity.Task;

import java.time.LocalDate;

public interface ITaskService {

    CreatedTaskResponse createTask(Long creatorId, CreatedTaskRequest request);

    AddTaskOwnerResponse addTaskOwner(AddTaskOwnerRequest request);

    void updateTask(Long taskId, LocalDate newDueDate, Task.Status newStatus, Long version);

    PageResponse<UnfinishedTask> getAllUnfinishedTasksOfUser(Long userId);

    /*
    các filter task theo mốc thời gian hoặc trạng thái:
       các task trong vòng còn hạn trong 1 tuần,
    * */

    PageResponse<TaskResponse> filterTasksOfUser(
            Long userId,
            Long teamId,
            Task.Status status,
            LocalDate first,
            LocalDate second,
            String sortBy);
}
