package com.java_spring_boot.first_demo.service.socket_service.listener;

import com.java_spring_boot.first_demo.dto.response.NotificationResponse;
import com.java_spring_boot.first_demo.entity.Notification;
import com.java_spring_boot.first_demo.service.socket_service.event.NotificationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Async("task-executor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotification(NotificationCreatedEvent event) {
        log.info("Websocket sending notification to broker...");
        Notification notification = event.getNotification();
        String destination = "/topic/notification-tasks/" + notification.getReceiver().getId();
        simpMessagingTemplate.convertAndSend(
                destination,
                NotificationResponse.fromEntity(notification)
        );
    }
}
