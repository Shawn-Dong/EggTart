package com.EggTart.dyst.EggTart.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelayTaskDto {
    
    @NotNull(message = "Delay minutes is required")
    @Min(value = 1, message = "Delay must be at least 1 minute")
    @Max(value = 1440, message = "Delay cannot exceed 24 hours")
    private Integer delayMinutes;
} 