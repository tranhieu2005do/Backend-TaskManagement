package com.java_spring_boot.first_demo.repository;

import com.java_spring_boot.first_demo.entity.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment,Long> {

    @Query("""
            SELECT ta.user.id FROM TaskAssignment ta
            WHERE ta.task.id = :taskId
            """)
    List<Long> findOwnerIdsByTaskId(@Param("taskId") Long taskId);

    boolean existsByTask_IdAndUser_Id(Long taskId, Long userId);
}
