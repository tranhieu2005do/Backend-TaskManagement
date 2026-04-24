package com.java_spring_boot.first_demo.service.impl;

import com.java_spring_boot.first_demo.custom_annotation.Audit;
import com.java_spring_boot.first_demo.dto.request.CreatedConversationRequest;
import com.java_spring_boot.first_demo.dto.response.ConversationResponse;
import com.java_spring_boot.first_demo.dto.response.CreatedConversationResponse;
import com.java_spring_boot.first_demo.entity.*;
import com.java_spring_boot.first_demo.exception.NotFoundException;
import com.java_spring_boot.first_demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository conversationParticipantRepository;
    private final TeamRepository teamRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreatedConversationResponse createConversation(CreatedConversationRequest request){
        log.info("Creating new conversation");
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new NotFoundException("Team not found"));
        Conversation newConversation = Conversation.builder()
                .type(request.getType())
                .name(request.getName())
                .lastActive(LocalDateTime.now())
                .team(team)
                .build();
        conversationRepository.save(newConversation);
        log.info("New conversation created");
        return CreatedConversationResponse.fromEntity(newConversation);
    }


    private ConversationResponse fromEntity(Conversation conversation){
        // Lấy tin nhắn cuối trong đoạn chat
        Message lastMessage = conversation.getMessage();
        return ConversationResponse.builder()
                .id(conversation.getId())
                .teamId(conversation.getTeam().getId())
                .name(conversation.getName())
                .lastSender(lastMessage != null ? lastMessage.getSender().getFullName() : null)
                .lastMessage(lastMessage == null ? null : lastMessage.getContent())
                .lastUpdated(conversation.getUpdatedAt())
                .lastActive(conversation.getLastActive())
                .numberOfMembers(conversationParticipantRepository.numberOfTeamMembersInConversation(conversation.getId()))
                .build();
    }

//    @Audit(action = "GET_CONVERSATIONS", entity = "Conversation")
    @PreAuthorize("@authSecurity.isCurrentUser(#userId)")
    @Transactional(readOnly = true)
    public List<ConversationResponse> getAllConversationsOfUser(Long userId){
        log.info("Getting all conversations of user with id {}", userId);
        return conversationParticipantRepository.getAllConversationsOfUser(userId)
                .stream()
                .map(this::fromEntity)
                .toList();
    }

    @Audit(action = "ADD_TO_CONVERSATION", entity = "ConversationParticipant")
    public void addToConversation(Long teamId, Long userId){
        log.info("Adding user with id {} to conversation of team with id {}",  userId, teamId);
        Conversation conversation = conversationRepository.findByTeam_Id(teamId);
        if(conversation == null){
            log.error("Not found conversation with team id {}", teamId);
            throw new NotFoundException("Conversation not found");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ConversationParticipant newParticipant = ConversationParticipant.builder()
                .conversation(conversation)
                .user(user)
                .joinedAt(LocalDateTime.now())
                .build();
        conversationParticipantRepository.save(newParticipant);
    }
}
