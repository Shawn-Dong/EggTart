package com.EggTart.dyst.EggTart.model.entity;

import com.EggTart.dyst.EggTart.model.enums.Mood;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "walk_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class WalkRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "task_id", nullable = false)
    private Long taskId;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean pee = false;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean poo = false;
    
    @Enumerated(EnumType.STRING)
    private Mood mood;
    
    @Column(name = "photo_url")
    private String photoURL;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
} 