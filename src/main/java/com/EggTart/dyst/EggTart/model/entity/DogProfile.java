package com.EggTart.dyst.EggTart.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dog_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"taskTemplates"})
public class DogProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Dog name is required")
    private String name;
    
    @Column(name = "age_months")
    @Min(value = 1, message = "Age must be positive")
    private Integer ageMo;
    
    @Column(precision = 5, scale = 2)
    @DecimalMin(value = "0.1", message = "Weight must be positive")
    private BigDecimal weight;
    
    @Column(name = "is_puppy")
    private Boolean puppyFlag;
    
    @Column(name = "meal_offset_minutes")
    @Builder.Default
    private Integer mealOffsetMinutes = 30;
    
    @Column(name = "drink_offset_minutes")
    @Builder.Default
    private Integer drinkOffsetMinutes = 15;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "dogId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskTemplate> taskTemplates = new ArrayList<>();
    
    @PrePersist
    private void prePersist() {
        if (puppyFlag == null) {
            puppyFlag = ageMo != null && ageMo <= 12;
        }
    }
} 