package com.java_spring_boot.first_demo.service.async_service;

import com.java_spring_boot.first_demo.dto.request.CreatedNotificationRequest;
import com.java_spring_boot.first_demo.service.impl.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationAsyncService {

    private final NotificationService notificationService;

    public void createNotificationAsync(CreatedNotificationRequest createdNotificationRequest) {
        log.info("Creating notification async service");
        notificationService.createNotification(createdNotificationRequest);
    }
}
