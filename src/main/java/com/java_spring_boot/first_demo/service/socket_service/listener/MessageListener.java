package com.java_spring_boot.first_demo.service.socket_service.listener;

import com.java_spring_boot.first_demo.dto.response.MessageResponse;
import com.java_spring_boot.first_demo.entity.Message;
import com.java_spring_boot.first_demo.service.socket_service.event.MessageSentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSendMessage(MessageSentEvent event){
        log.info("Received message event");
        Message message = event.getMessage();
        String destination = "/topic/conversations/" + message.getConversation().getId();

        simpMessagingTemplate.convertAndSend(
                destination,
                MessageResponse.fromEntity(message)
        );

        log.info("Sent message to topic: {}", destination);
    }
}
