package com.java_spring_boot.first_demo.service.socket_service.listener;

import com.java_spring_boot.first_demo.dto.response.CommentResponse;
import com.java_spring_boot.first_demo.entity.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommentListener {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSendComment(Comment comment) {
        log.info("Websocket sending comment to task topic...");
        // Gửi đến topic chung của Task để mọi người đang xem task đó đều nhận được
        String destination = "/topic/tasks/" + comment.getTask().getId() + "/comments";
        simpMessagingTemplate.convertAndSend(destination, CommentResponse.fromEntity(comment));
    }
}
