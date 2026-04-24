package com.java_spring_boot.first_demo.repository;

import com.java_spring_boot.first_demo.entity.Conversation;
import com.java_spring_boot.first_demo.entity.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant,Long> {

    @Query("""
            SELECT COUNT(cp) FROM ConversationParticipant cp
            WHERE cp.conversation.id = :conversationId
            """)
    Integer numberOfTeamMembersInConversation(@Param("conversationId") Long conversationId);

    @Query("""
            SELECT cp.conversation FROM ConversationParticipant cp
            WHERE cp.user.id = :userId
            ORDER BY cp.conversation.message.id DESC
            """)
    List<Conversation> getAllConversationsOfUser(@Param("userId") Long userId);

    boolean existsByUserIdAndConversationId(Long userId, Long conversationId);
}
