package com.java_spring_boot.first_demo.service.interf;

import com.java_spring_boot.first_demo.dto.request.CreatedNotificationRequest;
import com.java_spring_boot.first_demo.dto.response.NotificationResponse;
import com.java_spring_boot.first_demo.dto.response.PageResponse;
import com.java_spring_boot.first_demo.entity.Task;

public interface INotificationService {

    NotificationResponse createNotification(CreatedNotificationRequest request);

    void createReminderUpcomingTask(Task task);

    PageResponse<NotificationResponse> getAllNotifications(Long userId);
}
