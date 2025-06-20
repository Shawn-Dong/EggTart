package com.EggTart.dyst.EggTart.repository;

import com.EggTart.dyst.EggTart.model.entity.WalkRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalkRecordRepository extends JpaRepository<WalkRecord, Long> {
    
    @Query("SELECT w FROM WalkRecord w WHERE w.taskId = :taskId")
    WalkRecord findByTaskId(@Param("taskId") Long taskId);
    
    @Query("""
        SELECT w FROM WalkRecord w 
        WHERE w.startTime BETWEEN :startDate AND :endDate 
        ORDER BY w.startTime DESC
        """)
    List<WalkRecord> findByStartTimeBetweenOrderByStartTimeDesc(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
} 