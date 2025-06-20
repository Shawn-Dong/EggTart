package com.EggTart.dyst.EggTart.model.entity;

import com.EggTart.dyst.EggTart.model.enums.TaskType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class TaskTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "dog_id", nullable = false)
    private Long dogId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType type;
    
    @Column(name = "default_time", nullable = false)
    private LocalTime defaultTime;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
} 