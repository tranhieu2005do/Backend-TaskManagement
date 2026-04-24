package com.java_spring_boot.first_demo.repository;

import com.java_spring_boot.first_demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query("""
            SELECT c FROM Comment c
            WHERE c.task.id = :taskId
            """)
    List<Comment> getCommentsByTaskId(@Param("taskId") Long taskId);

    @Query("""
            SELECT c.task.team.id FROM Comment c
            WHERE c.id = :commentId
            """)
    Long findTeamIdByCommentId(@Param("commentId") Long commentId);

    boolean existsByIdAndCommenter_Id(Long commentId, Long commenterId);
}
