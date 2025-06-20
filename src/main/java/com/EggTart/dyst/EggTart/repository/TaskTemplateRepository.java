package com.EggTart.dyst.EggTart.repository;

import com.EggTart.dyst.EggTart.model.entity.TaskTemplate;
import com.EggTart.dyst.EggTart.model.enums.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskTemplateRepository extends JpaRepository<TaskTemplate, Long> {
    
    @Query("SELECT t FROM TaskTemplate t WHERE t.dogId = :dogId ORDER BY t.defaultTime")
    List<TaskTemplate> findByDogIdOrderByDefaultTime(@Param("dogId") Long dogId);
    
    @Query("SELECT t FROM TaskTemplate t WHERE t.dogId = :dogId AND t.type = :type ORDER BY t.defaultTime")
    List<TaskTemplate> findByDogIdAndTypeOrderByDefaultTime(
        @Param("dogId") Long dogId, 
        @Param("type") TaskType type
    );
} 