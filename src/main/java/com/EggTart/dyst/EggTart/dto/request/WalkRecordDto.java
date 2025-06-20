package com.EggTart.dyst.EggTart.dto.request;

import com.EggTart.dyst.EggTart.model.enums.Mood;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalkRecordDto {
    
    @NotNull(message = "Pee status is required")
    private Boolean pee;
    
    @NotNull(message = "Poo status is required")
    private Boolean poo;
    
    private Mood mood;
    
    private String photoURL;
    
    private String notes;
} 