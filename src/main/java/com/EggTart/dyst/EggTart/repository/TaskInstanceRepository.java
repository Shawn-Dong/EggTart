package com.EggTart.dyst.EggTart.repository;

import com.EggTart.dyst.EggTart.model.entity.TaskInstance;
import com.EggTart.dyst.EggTart.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskInstanceRepository extends JpaRepository<TaskInstance, Long> {
    
    @Query("""
        SELECT t FROM TaskInstance t 
        WHERE t.dogId = :dogId 
        AND t.scheduledTime BETWEEN :startDate AND :endDate 
        ORDER BY t.scheduledTime
        """)
    List<TaskInstance> findByDogIdAndScheduledTimeBetween(
        @Param("dogId") Long dogId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT COUNT(t) FROM TaskInstance t WHERE t.status = :status")
    long countByStatus(@Param("status") TaskStatus status);
    
    @Query("""
        SELECT t FROM TaskInstance t 
        WHERE t.dogId = :dogId 
        AND t.scheduledTime >= :startDate 
        AND t.status = :status
        ORDER BY t.scheduledTime
        """)
    List<TaskInstance> findByDogIdAndScheduledTimeAfterAndStatus(
        @Param("dogId") Long dogId,
        @Param("startDate") LocalDateTime startDate,
        @Param("status") TaskStatus status
    );
} 