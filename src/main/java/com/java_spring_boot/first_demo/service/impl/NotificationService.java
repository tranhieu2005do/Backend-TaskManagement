package com.java_spring_boot.first_demo.service.impl;

import com.java_spring_boot.first_demo.dto.request.CreatedNotificationRequest;
import com.java_spring_boot.first_demo.dto.response.NotificationResponse;
import com.java_spring_boot.first_demo.dto.response.PageResponse;
import com.java_spring_boot.first_demo.entity.Notification;
import com.java_spring_boot.first_demo.entity.Task;
import com.java_spring_boot.first_demo.entity.User;
import com.java_spring_boot.first_demo.exception.NotFoundException;
import com.java_spring_boot.first_demo.repository.NotificationRepository;
import com.java_spring_boot.first_demo.repository.UserRepository;
import com.java_spring_boot.first_demo.service.interf.INotificationService;
import com.java_spring_boot.first_demo.service.socket_service.event.NotificationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

//    @PreAuthorize("hasRole('SYSTEM')")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public NotificationResponse createNotification(CreatedNotificationRequest request) {
        log.info("Creating notification with request {}", request);
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new NotFoundException("User with id " + request.getReceiverId() + " not found"));

        Notification newNotification = Notification.builder()
                .receiver(receiver)
                .createdAt(LocalDateTime.now())
                .content(request.getContent())
                .isRead(false)
                .type(request.getType())
                .build();
        notificationRepository.save(newNotification);
        eventPublisher.publishEvent(new NotificationCreatedEvent(newNotification));
        log.info("Published notification with new Notification {}", newNotification);
        log.info("New notification has been created");
        return NotificationResponse.fromEntity(newNotification);
    }

//    @PreAuthorize("hasRole('SYSTEM')")
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createReminderUpcomingTask(Task task) {
        log.info("Creating reminder upcoming task with task {}", task.getTitle());
        List<User> receiver = userRepository.findOwnerOfTask(task.getId());

        for(User receiverUser : receiver) {
            Notification newNotification = Notification.builder()
                    .createdAt(LocalDateTime.now())
                    .content("This task is still not be done. Let's finishes it")
                    .receiver(receiverUser)
                    .type(Notification.Type.due)
                    .build();
            notificationRepository.save(newNotification);
        }
    }

    @PreAuthorize("@authSecurity.isCurrentUser(#userId)")
    @Transactional(readOnly = true)
    @Override
    public PageResponse<NotificationResponse> getAllNotifications(Long userId) {
        log.info("Getting all notifications for user with {}", userId);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        return PageResponse.fromPage(
                notificationRepository.getAllNotifications(userId, pageable)
                        .map(NotificationResponse::fromEntity)
        );
    }

    @PreAuthorize("@authSecurity.isCurrentUser(#userId)")
    public void markAllNotificationsAsRead(Long userId) {
        log.info("Marking all notifications as read for user with {}", userId);
        notificationRepository.markReadAllUnReadNotifications(userId);
    }

    @PreAuthorize("@authSecurity.isCurrentUser(#userId)")
    public void markUnreadNotification(Long notificationId){
        log.info("Marking unread notification with id {}", notificationId);
        notificationRepository.markUnreadNotification(notificationId);
    }

    @PreAuthorize("@authSecurity.isCurrentUser(#userId)")
    public void markReadNotification(Long notificationId){
        log.info("Marking read notification with id {}", notificationId);
        notificationRepository.markReadNotification(notificationId);
    }

    @PreAuthorize("@authSecurity.isCurrentUser(#userId)")
    public Integer getUnreadCountNotifications(Long userId){
        log.info("Getting unread count notifications for user with id {}", userId);
        return notificationRepository.getUncountNotifications(userId);
    }
}
