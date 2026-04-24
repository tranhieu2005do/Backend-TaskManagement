package com.java_spring_boot.first_demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
@AllArgsConstructor @NoArgsConstructor
@Data @Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;

    public enum Type{
        PRIVATE,GROUP
    }

    @Column(name = "name")
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    public Team team;

    @Column(name = "last_active")
    private LocalDateTime lastActive;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "last_message_id")
    private Message message;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
