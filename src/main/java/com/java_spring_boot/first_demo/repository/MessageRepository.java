package com.java_spring_boot.first_demo.repository;

import com.java_spring_boot.first_demo.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {

    @Query("""
            SELECT m FROM Message m
            WHERE m.conversation.id = :conversationId
               AND (:cursorId IS NULL OR m.id < :cursorId)
               ORDER BY m.id DESC
            """)
    List<Message> getMesssagesInConversation(
            @Param("conversationId") Long conversationId,
            @Param("cursorId") Long cursorId,
            Pageable pageable);
}
