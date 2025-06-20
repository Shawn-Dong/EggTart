package com.EggTart.dyst.EggTart.controller;

import com.EggTart.dyst.EggTart.dto.request.OnboardingDto;
import com.EggTart.dyst.EggTart.dto.response.ApiResponse;
import com.EggTart.dyst.EggTart.exception.EntityNotFoundException;
import com.EggTart.dyst.EggTart.model.entity.DogProfile;
import com.EggTart.dyst.EggTart.model.entity.TaskTemplate;
import com.EggTart.dyst.EggTart.service.DogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dogs")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
@Validated
@RequiredArgsConstructor
@Slf4j
public class DogController {
    
    private final DogService dogService;
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DogProfile>> getDogProfile(
            @PathVariable @NotNull @Min(1) Long id) {
        
        log.debug("GET /api/dogs/{}", id);
        
        DogProfile dogProfile = dogService.getDogProfile(id);
        
        return ResponseEntity.ok(ApiResponse.success(dogProfile));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<DogProfile>> createDogProfile(
            @RequestBody @Valid OnboardingDto onboardingDto) {
        
        log.info("POST /api/dogs - Creating dog: {}", onboardingDto.getDogName());
        
        DogProfile dogProfile = dogService.createDogProfile(onboardingDto);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(dogProfile, "Dog profile created successfully"));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DogProfile>> updateDogProfile(
            @PathVariable @NotNull @Min(1) Long id,
            @RequestBody @Valid OnboardingDto onboardingDto) {
        
        log.info("PUT /api/dogs/{} - Updating dog: {}", id, onboardingDto.getDogName());
        
        DogProfile dogProfile = dogService.updateDogProfile(id, onboardingDto);
        
        return ResponseEntity.ok(ApiResponse.success(dogProfile, "Dog profile updated successfully"));
    }
    
    @GetMapping("/{id}/templates")
    public ResponseEntity<ApiResponse<List<TaskTemplate>>> getTaskTemplates(
            @PathVariable @NotNull @Min(1) Long id) {
        
        log.debug("GET /api/dogs/{}/templates", id);
        
        List<TaskTemplate> templates = dogService.getTaskTemplates(id);
        
        return ResponseEntity.ok(ApiResponse.success(templates));
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }
} 