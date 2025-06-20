package com.EggTart.dyst.EggTart.repository;

import com.EggTart.dyst.EggTart.model.entity.TaskInstance;
import com.EggTart.dyst.EggTart.model.enums.TaskStatus;
import com.EggTart.dyst.EggTart.model.enums.TaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Task Instance Repository Tests")
class TaskInstanceRepositoryTest {

    private final TaskInstanceRepository taskInstanceRepository;
    private final TestEntityManager entityManager;

    private TaskInstance pendingTask;
    private TaskInstance inProgressTask;
    private TaskInstance completedTask;
    private TaskInstance futureTask;

    // Constructor injection - @Autowired is required for @DataJpaTest
    TaskInstanceRepositoryTest(
        @Autowired TaskInstanceRepository taskInstanceRepository,
        @Autowired TestEntityManager entityManager
    ) {
        this.taskInstanceRepository = taskInstanceRepository;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        // Clear database
        taskInstanceRepository.deleteAll();
        entityManager.flush();

        // Create test data
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today = LocalDate.now().atStartOfDay();

        pendingTask = TaskInstance.builder()
            .dogId(1L)
            .type(TaskType.WALK)
            .scheduledTime(today.plusHours(8))
            .status(TaskStatus.PENDING)
            .build();

        inProgressTask = TaskInstance.builder()
            .dogId(1L)
            .type(TaskType.MEAL)
            .scheduledTime(today.plusHours(12))
            .status(TaskStatus.IN_PROGRESS)
            .startTime(now.minusMinutes(10))
            .build();

        completedTask = TaskInstance.builder()
            .dogId(1L)
            .type(TaskType.WALK)
            .scheduledTime(today.plusHours(17))
            .status(TaskStatus.COMPLETED)
            .startTime(now.minusHours(1))
            .endTime(now.minusMinutes(30))
            .build();

        futureTask = TaskInstance.builder()
            .dogId(1L)
            .type(TaskType.DRINK)
            .scheduledTime(today.plusDays(1).plusHours(9))
            .status(TaskStatus.PENDING)
            .build();

        // Save test data
        pendingTask = taskInstanceRepository.save(pendingTask);
        inProgressTask = taskInstanceRepository.save(inProgressTask);
        completedTask = taskInstanceRepository.save(completedTask);
        futureTask = taskInstanceRepository.save(futureTask);
    }

    @Test
    @DisplayName("Should save and retrieve task instance")
    void shouldSaveAndRetrieveTaskInstance() {
        // Given
        TaskInstance newTask = TaskInstance.builder()
            .dogId(2L)
            .type(TaskType.WALK)
            .scheduledTime(LocalDateTime.now().plusHours(1))
            .status(TaskStatus.PENDING)
            .build();

        // When
        TaskInstance savedTask = taskInstanceRepository.save(newTask);
        TaskInstance retrievedTask = taskInstanceRepository.findById(savedTask.getId()).orElse(null);

        // Then
        assertThat(retrievedTask).isNotNull();
        assertThat(retrievedTask.getDogId()).isEqualTo(2L);
        assertThat(retrievedTask.getType()).isEqualTo(TaskType.WALK);
        assertThat(retrievedTask.getStatus()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    @DisplayName("Should find tasks by dog ID and date range")
    void shouldFindTasksByDogIdAndDateRange() {
        // Given
        Long dogId = 1L;
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        // When
        List<TaskInstance> tasks = taskInstanceRepository.findByDogIdAndScheduledTimeBetween(
            dogId, startOfDay, endOfDay);

        // Then
        assertThat(tasks).hasSize(3); // pending, inProgress, completed (today's tasks)
        assertThat(tasks).allMatch(task -> task.getDogId().equals(dogId));
        assertThat(tasks).allMatch(task -> 
            task.getScheduledTime().isAfter(startOfDay.minusNanos(1)) &&
            task.getScheduledTime().isBefore(endOfDay.plusNanos(1)));
    }

    @Test
    @DisplayName("Should return empty list for non-existent dog ID")
    void shouldReturnEmptyListForNonExistentDogId() {
        // Given
        Long nonExistentDogId = 999L;
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        // When
        List<TaskInstance> tasks = taskInstanceRepository.findByDogIdAndScheduledTimeBetween(
            nonExistentDogId, startOfDay, endOfDay);

        // Then
        assertThat(tasks).isEmpty();
    }

    @Test
    @DisplayName("Should count tasks by status")
    void shouldCountTasksByStatus() {
        // When
        long pendingCount = taskInstanceRepository.countByStatus(TaskStatus.PENDING);
        long inProgressCount = taskInstanceRepository.countByStatus(TaskStatus.IN_PROGRESS);
        long completedCount = taskInstanceRepository.countByStatus(TaskStatus.COMPLETED);

        // Then
        assertThat(pendingCount).isEqualTo(2); // pendingTask + futureTask
        assertThat(inProgressCount).isEqualTo(1); // inProgressTask
        assertThat(completedCount).isEqualTo(1); // completedTask
    }

    @Test
    @DisplayName("Should find tasks by dog ID, date after, and status")
    void shouldFindTasksByDogIdAndDateAfterAndStatus() {
        // Given
        Long dogId = 1L;
        LocalDateTime startDate = LocalDateTime.now().minusHours(1);
        TaskStatus status = TaskStatus.PENDING;

        // When
        List<TaskInstance> tasks = taskInstanceRepository.findByDogIdAndScheduledTimeAfterAndStatus(
            dogId, startDate, status);

        // Then
        assertThat(tasks).hasSize(2); // pendingTask + futureTask
        assertThat(tasks).allMatch(task -> task.getDogId().equals(dogId));
        assertThat(tasks).allMatch(task -> task.getStatus().equals(status));
        assertThat(tasks).allMatch(task -> task.getScheduledTime().isAfter(startDate));
    }

    @Test
    @DisplayName("Should update task status")
    void shouldUpdateTaskStatus() {
        // Given
        TaskInstance task = pendingTask;
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;

        // When
        task.setStatus(newStatus);
        task.setStartTime(LocalDateTime.now());
        TaskInstance updatedTask = taskInstanceRepository.save(task);

        // Then
        assertThat(updatedTask.getStatus()).isEqualTo(newStatus);
        assertThat(updatedTask.getStartTime()).isNotNull();

        // Verify in database
        TaskInstance retrievedTask = taskInstanceRepository.findById(task.getId()).orElse(null);
        assertThat(retrievedTask).isNotNull();
        assertThat(retrievedTask.getStatus()).isEqualTo(newStatus);
    }

    @Test
    @DisplayName("Should delete task instance")
    void shouldDeleteTaskInstance() {
        // Given
        Long taskId = pendingTask.getId();

        // When
        taskInstanceRepository.deleteById(taskId);
        entityManager.flush();

        // Then
        assertThat(taskInstanceRepository.findById(taskId)).isEmpty();
    }

    @Test
    @DisplayName("Should find all tasks")
    void shouldFindAllTasks() {
        // When
        List<TaskInstance> allTasks = taskInstanceRepository.findAll();

        // Then
        assertThat(allTasks).hasSize(4); // All test tasks
        assertThat(allTasks).extracting("dogId").containsOnly(1L);
    }
} 