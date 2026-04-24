package com.java_spring_boot.first_demo.repository;

import com.java_spring_boot.first_demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("""
            SELECT ta.user FROM TaskAssignment ta
            WHERE ta.task.id = :taskId
            """)
    List<User> findOwnerOfTask(@Param("taskId") Long taskId);

}
