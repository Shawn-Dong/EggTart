package com.EggTart.dyst.EggTart.controller;

import com.EggTart.dyst.EggTart.dto.request.DelayTaskDto;
import com.EggTart.dyst.EggTart.dto.request.WalkRecordDto;
import com.EggTart.dyst.EggTart.dto.response.ApiResponse;
import com.EggTart.dyst.EggTart.dto.response.TaskInstanceResponseDto;
import com.EggTart.dyst.EggTart.exception.EntityNotFoundException;
import com.EggTart.dyst.EggTart.exception.InvalidTaskStateException;
import com.EggTart.dyst.EggTart.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
@Validated
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    
    private final TaskService taskService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskInstanceResponseDto>>> getTasks(
            @RequestParam @NotNull @Min(1) Long dogId,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.debug("GET /api/tasks - dogId: {}, date: {}", dogId, date);
        
        List<TaskInstanceResponseDto> tasks = date != null 
            ? taskService.getTasksForDate(dogId, date)
            : taskService.getTodayTasks(dogId);
        
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<TaskInstanceResponseDto>> startTask(
            @PathVariable @NotNull @Min(1) Long id) {
        
        log.info("POST /api/tasks/{}/start", id);
        
        TaskInstanceResponseDto task = taskService.startTask(id);
        
        return ResponseEntity.ok(ApiResponse.success(task));
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<TaskInstanceResponseDto>> completeTask(
            @PathVariable @NotNull @Min(1) Long id,
            @RequestBody(required = false) @Valid WalkRecordDto walkRecordDto) {
        
        log.info("POST /api/tasks/{}/complete", id);
        
        TaskInstanceResponseDto task = taskService.completeTask(id, walkRecordDto);
        
        return ResponseEntity.ok(ApiResponse.success(task));
    }
    
    @PostMapping("/{id}/delay")
    public ResponseEntity<ApiResponse<TaskInstanceResponseDto>> delayTask(
            @PathVariable @NotNull @Min(1) Long id,
            @RequestBody @Valid DelayTaskDto delayDto) {
        
        log.info("POST /api/tasks/{}/delay - minutes: {}", id, delayDto.getDelayMinutes());
        
        TaskInstanceResponseDto task = taskService.delayTask(id, delayDto.getDelayMinutes());
        
        return ResponseEntity.ok(ApiResponse.success(task));
    }
    
    @PostMapping("/{id}/skip")
    public ResponseEntity<ApiResponse<TaskInstanceResponseDto>> skipTask(
            @PathVariable @NotNull @Min(1) Long id) {
        
        log.info("POST /api/tasks/{}/skip", id);
        
        TaskInstanceResponseDto task = taskService.skipTask(id);
        
        return ResponseEntity.ok(ApiResponse.success(task));
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(InvalidTaskStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidTaskState(InvalidTaskStateException ex) {
        log.warn("Invalid task state: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.getMessage()));
    }
} 