package com.EggTart.dyst.EggTart.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingDto {
    
    @NotBlank(message = "Dog name is required")
    @Size(min = 2, max = 50, message = "Dog name must be between 2 and 50 characters")
    private String dogName;
    
    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1 month")
    @Max(value = 300, message = "Age must be less than 300 months")
    private Integer ageMo;
    
    @DecimalMin(value = "0.1", message = "Weight must be positive")
    @DecimalMax(value = "200.0", message = "Weight must be reasonable")
    private java.math.BigDecimal weight;
    
    @NotEmpty(message = "At least one meal time is required")
    @Size(max = 5, message = "Maximum 5 meal times allowed")
    private List<@NotNull LocalTime> mealTimes;
    
    @NotEmpty(message = "At least one walk time is required")
    @Size(max = 10, message = "Maximum 10 walk times allowed")
    private List<@NotNull LocalTime> walkTimes;
    
    @Size(max = 5, message = "Maximum 5 drink times allowed")
    private List<@NotNull LocalTime> drinkTimes;
} 