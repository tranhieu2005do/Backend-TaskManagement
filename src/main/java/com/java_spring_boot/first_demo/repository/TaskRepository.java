package com.java_spring_boot.first_demo.repository;

import com.java_spring_boot.first_demo.entity.Task;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    @Query("""
            SELECT t FROM Task t
            WHERE t.team.id = :teamId
            """)
    List<Task> getTaskByTeamId(@Param("teamId") Long teamId);

    @Query("""
            SELECT ta.task FROM TaskAssignment ta
            WHERE ta.user.id = :userId 
                 AND ta.task.status <> :status
            """)
    Page<Task> getUnfinishedTasksOfUser(@Param("userId") Long userId, Pageable pageable, Task.Status status);

    /*
    fliter task của user theo trạng thái status,
         range due_date,
         theo team,

    * */
    @Query("""
            SELECT ta.task FROM TaskAssignment ta
            WHERE ta.user.id = :userId
                AND (:status IS NULL OR ta.task.status = :status)
                AND (:teamId IS NULL OR ta.task.team.id = :teamId)
                AND ((:first IS NULL OR :second IS NULL) OR ta.task.dueDate BETWEEN :first AND :second)
            """)
    Page<Task> filterTasksOfUser(
            @Param("userId") Long userId,
            Pageable pageable,
            @Param("teamId") Long teamId,
            @Param("first") LocalDate first,
            @Param("second") LocalDate second,
            @Param("status") Task.Status status);

    @Query("""
            SELECT t FROM Task t
            WHERE t.dueDate = :date
                  AND t.status <> :status
            """)
    List<Task> findTasksDueTomorrow(@Param("date") LocalDate tomorrow, Task.Status status);

    @Query("""
            SELECT COUNT(t) FROM Task t
            WHERE t.team.id = :teamId
            """)
    Integer getNumberOfTasksByTeamId(@Param("teamId") Long teamId);

    @Query("""
        SELECT t.team.id
        FROM Task t
        WHERE t.id = :taskId
    """)
    Long findTeamIdByTaskId(@Param("taskId") Long taskId);
}
