package com.java_spring_boot.first_demo.service.impl;

import com.java_spring_boot.first_demo.custom_annotation.Audit;
import com.java_spring_boot.first_demo.dto.response.MessageResponse;
import com.java_spring_boot.first_demo.dto.response.UploadResult;
import com.java_spring_boot.first_demo.entity.Conversation;
import com.java_spring_boot.first_demo.entity.Message;
import com.java_spring_boot.first_demo.entity.User;
import com.java_spring_boot.first_demo.exception.NotFoundException;
import com.java_spring_boot.first_demo.repository.ConversationRepository;
import com.java_spring_boot.first_demo.repository.MessageRepository;
import com.java_spring_boot.first_demo.repository.UserRepository;
import com.java_spring_boot.first_demo.service.socket_service.event.MessageSentEvent;
import com.java_spring_boot.first_demo.util.FileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CloudinaryService cloudinaryService;

    @Audit(action = "SEND_MESSAGE", entity = "Message")
    @PreAuthorize("@conversationSecurity.isInConversation(#conversationId)")
    public MessageResponse sendMessage(Long senderId, String content, Long conversationId, MultipartFile file)
            throws IOException {
        log.info("Sending message from sender with id {}", senderId);
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("sender not found"));

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new NotFoundException("conversation not found"));

        // String folder = "task-management/chat";
        // UploadResult result = cloudinaryService.upload(file, folder);
        // String type = result.getResourceType();
        Message newMessage = Message.builder()
                .sender(sender)
                .conversation(conversation)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
        if (file == null) {
            newMessage.setType(Message.Type.TEXT);
        } else {
            FileValidator.validate(file);
            String folder = "task-management/chat";
            UploadResult result = cloudinaryService.upload(file, folder);

            newMessage.setFileUrl(result.getUrl());
            newMessage.setFileName(result.getOriginalFileName());
            newMessage.setFileSize(result.getSize());

            // Map detected MIME to legacy IMAGE or FILE types
            if (FileValidator.isImage(result.getMimeType())) {
                newMessage.setType(Message.Type.IMAGE);
            } else {
                newMessage.setType(Message.Type.FILE);
            }
        }
        messageRepository.save(newMessage);
        log.info("Message sent successfully");
        // Update tin nhắn cuối cùng v thời gian cập nhật
        conversation.setMessage(newMessage);
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        applicationEventPublisher.publishEvent(new MessageSentEvent(newMessage));
        return MessageResponse.fromEntity(newMessage);
    }

    @PreAuthorize("@conversationSecurity.isInConversation(#conversationId)")
    public List<MessageResponse> getMessagesInConversation(Long conversationId, Long cursorId) {
        log.info("Retrieving messages in conversation...");
        Pageable request = PageRequest.of(0, 20);
        return messageRepository.getMesssagesInConversation(conversationId, cursorId, request)
                .stream()
                .map(MessageResponse::fromEntity)
                .toList();
    }
}
