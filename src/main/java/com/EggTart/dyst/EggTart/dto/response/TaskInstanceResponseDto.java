package com.EggTart.dyst.EggTart.dto.response;

import com.EggTart.dyst.EggTart.model.entity.TaskInstance;
import com.EggTart.dyst.EggTart.model.enums.TaskStatus;
import com.EggTart.dyst.EggTart.model.enums.TaskType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskInstanceResponseDto {
    
    @NotNull(message = "Task ID is required")
    private Long id;
    
    @NotNull(message = "Task type is required")
    private TaskType type;
    
    @NotNull(message = "Scheduled time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduledTime;
    
    @NotNull(message = "Status is required")
    private TaskStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime countdownStartTime;
    
    // Conversion methods
    public static TaskInstanceResponseDto from(TaskInstance entity) {
        return TaskInstanceResponseDto.builder()
            .id(entity.getId())
            .type(entity.getType())
            .scheduledTime(entity.getScheduledTime())
            .status(entity.getStatus())
            .startTime(entity.getStartTime())
            .endTime(entity.getEndTime())
            .countdownStartTime(entity.getCountdownStartTime())
            .build();
    }
} 