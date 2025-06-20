package com.EggTart.dyst.EggTart.service;

import com.EggTart.dyst.EggTart.dto.request.WalkRecordDto;
import com.EggTart.dyst.EggTart.dto.response.TaskInstanceResponseDto;
import com.EggTart.dyst.EggTart.exception.EntityNotFoundException;
import com.EggTart.dyst.EggTart.exception.InvalidTaskStateException;
import com.EggTart.dyst.EggTart.model.entity.TaskInstance;
import com.EggTart.dyst.EggTart.model.entity.WalkRecord;
import com.EggTart.dyst.EggTart.model.enums.Mood;
import com.EggTart.dyst.EggTart.model.enums.TaskStatus;
import com.EggTart.dyst.EggTart.model.enums.TaskType;
import com.EggTart.dyst.EggTart.repository.TaskInstanceRepository;
import com.EggTart.dyst.EggTart.repository.WalkRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Task Service Tests")
class TaskServiceTest {

    @Mock
    private TaskInstanceRepository taskInstanceRepository;

    @Mock
    private WalkRecordRepository walkRecordRepository;

    @InjectMocks
    private TaskService taskService;

    private TaskInstance pendingTask;
    private TaskInstance inProgressTask;
    private TaskInstance completedTask;
    private WalkRecordDto walkRecordDto;

    @BeforeEach
    void setUp() {
        // Setup test data
        pendingTask = TaskInstance.builder()
            .id(1L)
            .dogId(1L)
            .type(TaskType.WALK)
            .scheduledTime(LocalDateTime.now())
            .status(TaskStatus.PENDING)
            .build();

        inProgressTask = TaskInstance.builder()
            .id(2L)
            .dogId(1L)
            .type(TaskType.MEAL)
            .scheduledTime(LocalDateTime.now())
            .status(TaskStatus.IN_PROGRESS)
            .startTime(LocalDateTime.now().minusMinutes(10))
            .build();

        completedTask = TaskInstance.builder()
            .id(3L)
            .dogId(1L)
            .type(TaskType.WALK)
            .scheduledTime(LocalDateTime.now().minusHours(1))
            .status(TaskStatus.COMPLETED)
            .startTime(LocalDateTime.now().minusHours(1))
            .endTime(LocalDateTime.now().minusMinutes(30))
            .build();

        walkRecordDto = WalkRecordDto.builder()
            .pee(true)
            .poo(false)
            .mood(Mood.HAPPY)
            .notes("Great walk!")
            .build();
    }

    @Test
    @DisplayName("Should get today's tasks successfully")
    void shouldGetTodayTasks() {
        // Given
        Long dogId = 1L;
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        
        List<TaskInstance> tasks = Arrays.asList(pendingTask, inProgressTask);
        when(taskInstanceRepository.findByDogIdAndScheduledTimeBetween(dogId, startOfDay, endOfDay))
            .thenReturn(tasks);

        // When
        List<TaskInstanceResponseDto> result = taskService.getTodayTasks(dogId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        
        verify(taskInstanceRepository).findByDogIdAndScheduledTimeBetween(dogId, startOfDay, endOfDay);
    }

    @Test
    @DisplayName("Should get tasks for specific date")
    void shouldGetTasksForSpecificDate() {
        // Given
        Long dogId = 1L;
        LocalDate testDate = LocalDate.of(2024, 1, 15);
        LocalDateTime startOfDay = testDate.atStartOfDay();
        LocalDateTime endOfDay = testDate.atTime(23, 59, 59);
        
        List<TaskInstance> tasks = Arrays.asList(pendingTask);
        when(taskInstanceRepository.findByDogIdAndScheduledTimeBetween(dogId, startOfDay, endOfDay))
            .thenReturn(tasks);

        // When
        List<TaskInstanceResponseDto> result = taskService.getTasksForDate(dogId, testDate);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        
        verify(taskInstanceRepository).findByDogIdAndScheduledTimeBetween(dogId, startOfDay, endOfDay);
    }

    @Test
    @DisplayName("Should start pending task successfully")
    void shouldStartPendingTask() {
        // Given
        Long taskId = 1L;
        when(taskInstanceRepository.findById(taskId)).thenReturn(Optional.of(pendingTask));
        when(taskInstanceRepository.save(any(TaskInstance.class))).thenReturn(pendingTask);

        // When
        TaskInstanceResponseDto result = taskService.startTask(taskId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(result.getStartTime()).isNotNull();
        assertThat(result.getCountdownStartTime()).isNotNull();
        
        verify(taskInstanceRepository).findById(taskId);
        verify(taskInstanceRepository).save(any(TaskInstance.class));
    }

    @Test
    @DisplayName("Should throw exception when starting non-pending task")
    void shouldThrowExceptionWhenStartingNonPendingTask() {
        // Given
        Long taskId = 2L;
        when(taskInstanceRepository.findById(taskId)).thenReturn(Optional.of(inProgressTask));

        // When & Then
        assertThatThrownBy(() -> taskService.startTask(taskId))
            .isInstanceOf(InvalidTaskStateException.class)
            .hasMessageContaining("Cannot start task in status: IN_PROGRESS");
        
        verify(taskInstanceRepository).findById(taskId);
        verify(taskInstanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when task not found")
    void shouldThrowExceptionWhenTaskNotFound() {
        // Given
        Long taskId = 999L;
        when(taskInstanceRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.startTask(taskId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Task not found with ID: 999");
        
        verify(taskInstanceRepository).findById(taskId);
        verify(taskInstanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should complete task successfully")
    void shouldCompleteTaskSuccessfully() {
        // Given
        Long taskId = 2L;
        when(taskInstanceRepository.findById(taskId)).thenReturn(Optional.of(inProgressTask));
        when(taskInstanceRepository.save(any(TaskInstance.class))).thenReturn(inProgressTask);

        // When
        TaskInstanceResponseDto result = taskService.completeTask(taskId, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(result.getEndTime()).isNotNull();
        
        verify(taskInstanceRepository).findById(taskId);
        verify(taskInstanceRepository).save(any(TaskInstance.class));
        verify(walkRecordRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should complete walk task with walk record")
    void shouldCompleteWalkTaskWithWalkRecord() {
        // Given
        Long taskId = 1L;
        when(taskInstanceRepository.findById(taskId)).thenReturn(Optional.of(pendingTask));
        when(taskInstanceRepository.save(any(TaskInstance.class))).thenReturn(pendingTask);
        when(walkRecordRepository.save(any(WalkRecord.class))).thenReturn(new WalkRecord());

        // When
        TaskInstanceResponseDto result = taskService.completeTask(taskId, walkRecordDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        
        verify(taskInstanceRepository).findById(taskId);
        verify(taskInstanceRepository).save(any(TaskInstance.class));
        verify(walkRecordRepository).save(any(WalkRecord.class));
    }

    @Test
    @DisplayName("Should throw exception when completing non-active task")
    void shouldThrowExceptionWhenCompletingNonActiveTask() {
        // Given
        Long taskId = 3L;
        when(taskInstanceRepository.findById(taskId)).thenReturn(Optional.of(completedTask));

        // When & Then
        assertThatThrownBy(() -> taskService.completeTask(taskId, null))
            .isInstanceOf(InvalidTaskStateException.class)
            .hasMessageContaining("Cannot complete task in status: COMPLETED");
        
        verify(taskInstanceRepository).findById(taskId);
        verify(taskInstanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delay task successfully")
    void shouldDelayTaskSuccessfully() {
        // Given
        Long taskId = 2L;
        Integer delayMinutes = 15;
        when(taskInstanceRepository.findById(taskId)).thenReturn(Optional.of(inProgressTask));
        when(taskInstanceRepository.save(any(TaskInstance.class))).thenReturn(inProgressTask);

        // When
        TaskInstanceResponseDto result = taskService.delayTask(taskId, delayMinutes);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(TaskStatus.PENDING); // Should reset to pending
        assertThat(result.getStartTime()).isNull(); // Should clear start time
        assertThat(result.getCountdownStartTime()).isNull(); // Should clear countdown
        
        verify(taskInstanceRepository).findById(taskId);
        verify(taskInstanceRepository).save(any(TaskInstance.class));
    }

    @Test
    @DisplayName("Should throw exception when delaying non-active task")
    void shouldThrowExceptionWhenDelayingNonActiveTask() {
        // Given
        Long taskId = 3L;
        Integer delayMinutes = 15;
        when(taskInstanceRepository.findById(taskId)).thenReturn(Optional.of(completedTask));

        // When & Then
        assertThatThrownBy(() -> taskService.delayTask(taskId, delayMinutes))
            .isInstanceOf(InvalidTaskStateException.class)
            .hasMessageContaining("Cannot delay task in status: COMPLETED");
        
        verify(taskInstanceRepository).findById(taskId);
        verify(taskInstanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should skip task successfully")
    void shouldSkipTaskSuccessfully() {
        // Given
        Long taskId = 1L;
        when(taskInstanceRepository.findById(taskId)).thenReturn(Optional.of(pendingTask));
        when(taskInstanceRepository.save(any(TaskInstance.class))).thenReturn(pendingTask);

        // When
        TaskInstanceResponseDto result = taskService.skipTask(taskId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(TaskStatus.SKIPPED);
        assertThat(result.getEndTime()).isNotNull();
        
        verify(taskInstanceRepository).findById(taskId);
        verify(taskInstanceRepository).save(any(TaskInstance.class));
    }

    @Test
    @DisplayName("Should throw exception when skipping completed task")
    void shouldThrowExceptionWhenSkippingCompletedTask() {
        // Given
        Long taskId = 3L;
        when(taskInstanceRepository.findById(taskId)).thenReturn(Optional.of(completedTask));

        // When & Then
        assertThatThrownBy(() -> taskService.skipTask(taskId))
            .isInstanceOf(InvalidTaskStateException.class)
            .hasMessageContaining("Cannot skip completed task in status: COMPLETED");
        
        verify(taskInstanceRepository).findById(taskId);
        verify(taskInstanceRepository, never()).save(any());
    }
} 