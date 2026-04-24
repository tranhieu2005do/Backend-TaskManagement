package com.java_spring_boot.first_demo.repository;

import com.java_spring_boot.first_demo.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation,Long> {

    Conversation findByTeam_Id(Long teamId);
}
