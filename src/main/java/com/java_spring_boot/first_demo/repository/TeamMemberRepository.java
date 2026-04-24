package com.java_spring_boot.first_demo.repository;

import com.java_spring_boot.first_demo.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember,Long> {

    boolean existsByUserIdAndTeamIdAndActiveTrue(Long userId, Long teamId);

    boolean existsByUser_IdAndTeam_IdAndRoleAndActiveTrue(
            Long userId,
            Long teamId,
            TeamMember.Role_Team role
    );

    @Query("""
            SELECT tm FROM TeamMember tm
            WHERE tm.team.id = :teamId
            """)
    List<TeamMember> getTeamMembersByTeamId(@Param("teamId") Long teamId);


    @Query("""
            SELECT COUNT(tm) FROM TeamMember tm
            WHERE tm.team.id = :teamId
            """)
    Integer getNumberOfTeamMembersByTeamId(@Param("teamId") Long teamId);

    @Query("""
            SELECT tm FROM TeamMember tm
            WHERE tm.team.id = :teamId
               AND tm.user.email = :email
            """)
    TeamMember getTeamMemberByTeamIdAndEmail(
            @Param("teamId") Long teamId,
            @Param("email") String email
    );

    @Query("""
            SELECT tm.user.fullName FROM TeamMember tm
            WHERE tm.team.id = :teamId
               AND tm.active = true
            """)
    List<String> getAllNameOfTeamMembersByTeamId(@Param("teamId") Long teamId);
}
