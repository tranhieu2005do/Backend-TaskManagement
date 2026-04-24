package com.java_spring_boot.first_demo.repository;

import com.java_spring_boot.first_demo.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {
    @Query("""
            SELECT t FROM Team t
            JOIN TeamMember tm
               ON t.id = tm.team.id
               AND tm.user.id = :userId
            """)
    Page<Team> findAllTeamByUserId(@Param("userId") Long userId, Pageable pageable);
}
