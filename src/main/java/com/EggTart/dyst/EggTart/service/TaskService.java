package com.EggTart.dyst.EggTart.service;

import com.EggTart.dyst.EggTart.dto.request.WalkRecordDto;
import com.EggTart.dyst.EggTart.dto.response.TaskInstanceResponseDto;
import com.EggTart.dyst.EggTart.exception.EntityNotFoundException;
import com.EggTart.dyst.EggTart.exception.InvalidTaskStateException;
import com.EggTart.dyst.EggTart.model.entity.TaskInstance;
import com.EggTart.dyst.EggTart.model.entity.WalkRecord;
import com.EggTart.dyst.EggTart.model.enums.TaskStatus;
import com.EggTart.dyst.EggTart.repository.TaskInstanceRepository;
import com.EggTart.dyst.EggTart.repository.WalkRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskInstanceRepository taskInstanceRepository;
    private final WalkRecordRepository walkRecordRepository;
    
    @Transactional(readOnly = true)
    public List<TaskInstanceResponseDto> getTodayTasks(Long dogId) {
        log.debug("Fetching today's tasks for dog ID: {}", dogId);
        
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        
        List<TaskInstance> tasks = taskInstanceRepository
            .findByDogIdAndScheduledTimeBetween(dogId, startOfDay, endOfDay);
        
        log.info("Found {} tasks for dog ID: {} on {}", tasks.size(), dogId, today);
        
        return tasks.stream()
            .map(TaskInstanceResponseDto::from)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TaskInstanceResponseDto> getTasksForDate(Long dogId, LocalDate date) {
        log.debug("Fetching tasks for dog ID: {} on date: {}", dogId, date);
        
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        
        List<TaskInstance> tasks = taskInstanceRepository
            .findByDogIdAndScheduledTimeBetween(dogId, startOfDay, endOfDay);
        
        return tasks.stream()
            .map(TaskInstanceResponseDto::from)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public TaskInstanceResponseDto startTask(Long taskId) {
        log.info("Starting task with ID: {}", taskId);
        
        TaskInstance task = findTaskById(taskId);
        
        // Validate state transition
        if (task.getStatus() != TaskStatus.PENDING) {
            throw new InvalidTaskStateException(
                String.format("Cannot start task in status: %s", task.getStatus())
            );
        }
        
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setStartTime(LocalDateTime.now());
        task.setCountdownStartTime(LocalDateTime.now());
        
        TaskInstance savedTask = taskInstanceRepository.save(task);
        
        log.info("Task {} started successfully", taskId);
        return TaskInstanceResponseDto.from(savedTask);
    }
    
    @Transactional
    public TaskInstanceResponseDto completeTask(Long taskId, WalkRecordDto walkRecordDto) {
        log.info("Completing task with ID: {}", taskId);
        
        TaskInstance task = findTaskById(taskId);
        
        // Validate state
        if (!task.getStatus().isActive()) {
            throw new InvalidTaskStateException(
                String.format("Cannot complete task in status: %s", task.getStatus())
            );
        }
        
        // Update task
        task.setStatus(TaskStatus.COMPLETED);
        task.setEndTime(LocalDateTime.now());
        
        // Create walk record if provided
        if (walkRecordDto != null && task.getType().name().equals("WALK")) {
            createWalkRecord(task, walkRecordDto);
        }
        
        TaskInstance savedTask = taskInstanceRepository.save(task);
        
        log.info("Task {} completed successfully", taskId);
        return TaskInstanceResponseDto.from(savedTask);
    }
    
    @Transactional
    public TaskInstanceResponseDto delayTask(Long taskId, Integer delayMinutes) {
        log.info("Delaying task {} by {} minutes", taskId, delayMinutes);
        
        TaskInstance task = findTaskById(taskId);
        
        // Validate state
        if (!task.getStatus().isActive()) {
            throw new InvalidTaskStateException(
                String.format("Cannot delay task in status: %s", task.getStatus())
            );
        }
        
        // Update scheduled time
        LocalDateTime newScheduledTime = task.getScheduledTime().plusMinutes(delayMinutes);
        task.setScheduledTime(newScheduledTime);
        
        // If task was in progress, reset to pending
        if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            task.setStatus(TaskStatus.PENDING);
            task.setStartTime(null);
            task.setCountdownStartTime(null);
        }
        
        TaskInstance savedTask = taskInstanceRepository.save(task);
        
        log.info("Task {} delayed successfully", taskId);
        return TaskInstanceResponseDto.from(savedTask);
    }
    
    @Transactional
    public TaskInstanceResponseDto skipTask(Long taskId) {
        log.info("Skipping task with ID: {}", taskId);
        
        TaskInstance task = findTaskById(taskId);
        
        // Validate state
        if (task.getStatus().isCompleted()) {
            throw new InvalidTaskStateException(
                String.format("Cannot skip completed task in status: %s", task.getStatus())
            );
        }
        
        task.setStatus(TaskStatus.SKIPPED);
        task.setEndTime(LocalDateTime.now());
        
        TaskInstance savedTask = taskInstanceRepository.save(task);
        
        log.info("Task {} skipped successfully", taskId);
        return TaskInstanceResponseDto.from(savedTask);
    }
    
    private TaskInstance findTaskById(Long taskId) {
        return taskInstanceRepository.findById(taskId)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Task not found with ID: %s", taskId)
            ));
    }
    
    private void createWalkRecord(TaskInstance task, WalkRecordDto dto) {
        WalkRecord record = WalkRecord.builder()
            .taskId(task.getId())
            .startTime(task.getStartTime() != null ? task.getStartTime() : LocalDateTime.now())
            .endTime(LocalDateTime.now())
            .pee(dto.getPee())
            .poo(dto.getPoo())
            .mood(dto.getMood())
            .photoURL(dto.getPhotoURL())
            .notes(dto.getNotes())
            .build();
        
        walkRecordRepository.save(record);
        log.debug("Walk record created for task {}", task.getId());
    }
} 