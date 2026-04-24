package com.java_spring_boot.first_demo.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "teams")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // orphanRemoval : true -> remove tasks -> delete task
    //                 false -> update task.team.id = null
    @OneToMany(
            orphanRemoval = true,
            fetch = FetchType.LAZY,
            mappedBy = "team",
            cascade = CascadeType.ALL
    )
    private List<Task> tasks;

    // cascade: persist: lưu lan xuống child
    //          merge: update lan xuống child
    //          remove: remove lan xuống child -> remove team thì sẽ remove hết task thuộc team
    //          refresh:
    //          detach
    //          all : = persist + merge + remove + refresh + detach, làm gì đến cha thì cũng ảnh hưởng đến con
}
