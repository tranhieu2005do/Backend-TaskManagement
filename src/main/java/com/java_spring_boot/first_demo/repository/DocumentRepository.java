package com.java_spring_boot.first_demo.repository;

import com.java_spring_boot.first_demo.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository extends JpaRepository<Document,Long> {

    @Query("""
            SELECT d FROM Document d
            WHERE d.task.id = :taskId
            """)
    Document findByTaskId(@Param("taskId") Long taskId);

    @Query("""
            SELECT d.task.id FROM Document d
            WHERE d.id = :documentId
            """)
    Long findTaskIdByDocumentId(@Param("documentId") Long documentId);

    @Query("""
            SELECT d.task.team.id
            FROM Document d
            WHERE d.id = :documentId
            """)
    Long findTeamIdByDocumentId(@Param("documentId") Long documentId);
}
