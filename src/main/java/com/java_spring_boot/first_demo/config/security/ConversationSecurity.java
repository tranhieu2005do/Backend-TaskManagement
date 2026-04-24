package com.java_spring_boot.first_demo.config.security;

import com.java_spring_boot.first_demo.repository.ConversationParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversationSecurity {

    private final AuthSecurity authSecurity;
    private final ConversationParticipantRepository conversationParticipant;

    public boolean isInConversation(Long conversationId){
        Long userId = authSecurity.getCurrentUserId();
        return userId != null
                && conversationParticipant.existsByUserIdAndConversationId(userId, conversationId);
    }
}
