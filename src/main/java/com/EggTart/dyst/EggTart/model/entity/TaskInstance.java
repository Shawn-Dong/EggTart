package com.EggTart.dyst.EggTart.model.entity;

import com.EggTart.dyst.EggTart.model.enums.TaskStatus;
import com.EggTart.dyst.EggTart.model.enums.TaskType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_instances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class TaskInstance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "task_template_id")
    private Long taskTemplateId;
    
    @Column(name = "dog_id", nullable = false)
    private Long dogId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType type;
    
    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskStatus status = TaskStatus.PENDING;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "countdown_start_time")
    private LocalDateTime countdownStartTime;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 