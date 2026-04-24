package com.java_spring_boot.first_demo.repository;

import com.java_spring_boot.first_demo.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    @Query("""
            SELECT n FROM Notification n
            WHERE n.receiver.id = :userId
            """)
    Page<Notification> getAllNotifications(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Notification n
            SET n.isRead = true
            WHERE n.isRead = false
              AND n.receiver.id = :user_id
            """)
    void markReadAllUnReadNotifications(@Param("user_id") Long userId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Notification n
            SET n.isRead = false
            WHERE n.id = :id
            """)
    void markUnreadNotification(@Param("id") Long notificationId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Notification n
            SET n.isRead = true
            WHERE n.id = :id
            """)
    void markReadNotification(@Param("id") Long notificationId);

    @Query("""
            SELECT COUNT(n) FROM Notification n
            WHERE n.receiver.id = :userId
               AND isRead = false
            """)
    Integer getUncountNotifications(@Param("userId") Long userId);
}
