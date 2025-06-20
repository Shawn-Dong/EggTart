package com.EggTart.dyst.EggTart.repository;

import com.EggTart.dyst.EggTart.model.entity.TaskTemplate;
import com.EggTart.dyst.EggTart.model.enums.TaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Task Template Repository Tests")
class TaskTemplateRepositoryTest {

    private final TaskTemplateRepository taskTemplateRepository;
    private final TestEntityManager entityManager;

    private TaskTemplate mealTemplate1;
    private TaskTemplate mealTemplate2;
    private TaskTemplate walkTemplate1;
    private TaskTemplate walkTemplate2;
    private TaskTemplate drinkTemplate;

    // Constructor injection - @Autowired is required for @DataJpaTest
    TaskTemplateRepositoryTest(
        @Autowired TaskTemplateRepository taskTemplateRepository,
        @Autowired TestEntityManager entityManager
    ) {
        this.taskTemplateRepository = taskTemplateRepository;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        // Clear database
        taskTemplateRepository.deleteAll();
        entityManager.flush();

        // Create test data for dog 1
        mealTemplate1 = TaskTemplate.builder()
            .dogId(1L)
            .type(TaskType.MEAL)
            .defaultTime(LocalTime.of(8, 0))
            .build();

        mealTemplate2 = TaskTemplate.builder()
            .dogId(1L)
            .type(TaskType.MEAL)
            .defaultTime(LocalTime.of(18, 0))
            .build();

        walkTemplate1 = TaskTemplate.builder()
            .dogId(1L)
            .type(TaskType.WALK)
            .defaultTime(LocalTime.of(7, 0))
            .build();

        walkTemplate2 = TaskTemplate.builder()
            .dogId(1L)
            .type(TaskType.WALK)
            .defaultTime(LocalTime.of(17, 0))
            .build();

        drinkTemplate = TaskTemplate.builder()
            .dogId(1L)
            .type(TaskType.DRINK)
            .defaultTime(LocalTime.of(9, 0))
            .build();

        // Save test data
        mealTemplate1 = taskTemplateRepository.save(mealTemplate1);
        mealTemplate2 = taskTemplateRepository.save(mealTemplate2);
        walkTemplate1 = taskTemplateRepository.save(walkTemplate1);
        walkTemplate2 = taskTemplateRepository.save(walkTemplate2);
        drinkTemplate = taskTemplateRepository.save(drinkTemplate);
    }

    @Test
    @DisplayName("Should save and retrieve task template")
    void shouldSaveAndRetrieveTaskTemplate() {
        // Given
        TaskTemplate newTemplate = TaskTemplate.builder()
            .dogId(2L)
            .type(TaskType.MEAL)
            .defaultTime(LocalTime.of(12, 0))
            .build();

        // When
        TaskTemplate savedTemplate = taskTemplateRepository.save(newTemplate);
        TaskTemplate retrievedTemplate = taskTemplateRepository.findById(savedTemplate.getId()).orElse(null);

        // Then
        assertThat(retrievedTemplate).isNotNull();
        assertThat(retrievedTemplate.getDogId()).isEqualTo(2L);
        assertThat(retrievedTemplate.getType()).isEqualTo(TaskType.MEAL);
        assertThat(retrievedTemplate.getDefaultTime()).isEqualTo(LocalTime.of(12, 0));
    }

    @Test
    @DisplayName("Should find task templates by dog ID ordered by default time")
    void shouldFindTaskTemplatesByDogIdOrderedByDefaultTime() {
        // When
        List<TaskTemplate> templates = taskTemplateRepository.findByDogIdOrderByDefaultTime(1L);

        // Then
        assertThat(templates).hasSize(5);
        assertThat(templates).allMatch(template -> template.getDogId().equals(1L));
        
        // Verify ordering by default time
        assertThat(templates).extracting("defaultTime")
            .containsExactly(
                LocalTime.of(7, 0),  // walkTemplate1
                LocalTime.of(8, 0),  // mealTemplate1
                LocalTime.of(9, 0),  // drinkTemplate
                LocalTime.of(17, 0), // walkTemplate2
                LocalTime.of(18, 0)  // mealTemplate2
            );
    }

    @Test
    @DisplayName("Should find task templates by dog ID and type ordered by default time")
    void shouldFindTaskTemplatesByDogIdAndTypeOrderedByDefaultTime() {
        // When
        List<TaskTemplate> mealTemplates = taskTemplateRepository.findByDogIdAndTypeOrderByDefaultTime(1L, TaskType.MEAL);
        List<TaskTemplate> walkTemplates = taskTemplateRepository.findByDogIdAndTypeOrderByDefaultTime(1L, TaskType.WALK);
        List<TaskTemplate> drinkTemplates = taskTemplateRepository.findByDogIdAndTypeOrderByDefaultTime(1L, TaskType.DRINK);

        // Then
        assertThat(mealTemplates).hasSize(2);
        assertThat(mealTemplates).allMatch(template -> template.getType().equals(TaskType.MEAL));
        assertThat(mealTemplates).extracting("defaultTime")
            .containsExactly(LocalTime.of(8, 0), LocalTime.of(18, 0));

        assertThat(walkTemplates).hasSize(2);
        assertThat(walkTemplates).allMatch(template -> template.getType().equals(TaskType.WALK));
        assertThat(walkTemplates).extracting("defaultTime")
            .containsExactly(LocalTime.of(7, 0), LocalTime.of(17, 0));

        assertThat(drinkTemplates).hasSize(1);
        assertThat(drinkTemplates).allMatch(template -> template.getType().equals(TaskType.DRINK));
        assertThat(drinkTemplates).extracting("defaultTime")
            .containsExactly(LocalTime.of(9, 0));
    }

    @Test
    @DisplayName("Should return empty list for non-existent dog ID")
    void shouldReturnEmptyListForNonExistentDogId() {
        // When
        List<TaskTemplate> templates = taskTemplateRepository.findByDogIdOrderByDefaultTime(999L);

        // Then
        assertThat(templates).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list for non-existent dog ID and type")
    void shouldReturnEmptyListForNonExistentDogIdAndType() {
        // When
        List<TaskTemplate> templates = taskTemplateRepository.findByDogIdAndTypeOrderByDefaultTime(999L, TaskType.MEAL);

        // Then
        assertThat(templates).isEmpty();
    }

    @Test
    @DisplayName("Should update task template")
    void shouldUpdateTaskTemplate() {
        // Given
        LocalTime newTime = LocalTime.of(10, 30);
        TaskType newType = TaskType.DRINK;

        // When
        mealTemplate1.setDefaultTime(newTime);
        mealTemplate1.setType(newType);
        TaskTemplate updatedTemplate = taskTemplateRepository.save(mealTemplate1);

        // Then
        assertThat(updatedTemplate.getDefaultTime()).isEqualTo(newTime);
        assertThat(updatedTemplate.getType()).isEqualTo(newType);

        // Verify in database
        TaskTemplate retrievedTemplate = taskTemplateRepository.findById(mealTemplate1.getId()).orElse(null);
        assertThat(retrievedTemplate).isNotNull();
        assertThat(retrievedTemplate.getDefaultTime()).isEqualTo(newTime);
        assertThat(retrievedTemplate.getType()).isEqualTo(newType);
    }

    @Test
    @DisplayName("Should delete task template")
    void shouldDeleteTaskTemplate() {
        // Given
        Long templateId = mealTemplate1.getId();

        // When
        taskTemplateRepository.deleteById(templateId);
        entityManager.flush();

        // Then
        assertThat(taskTemplateRepository.findById(templateId)).isEmpty();
        assertThat(taskTemplateRepository.findByDogIdOrderByDefaultTime(1L)).hasSize(4);
    }

    @Test
    @DisplayName("Should find all task templates")
    void shouldFindAllTaskTemplates() {
        // When
        List<TaskTemplate> allTemplates = taskTemplateRepository.findAll();

        // Then
        assertThat(allTemplates).hasSize(5);
        assertThat(allTemplates).extracting("dogId").containsOnly(1L);
    }

    @Test
    @DisplayName("Should count total task templates")
    void shouldCountTotalTaskTemplates() {
        // When
        long count = taskTemplateRepository.count();

        // Then
        assertThat(count).isEqualTo(5);
    }

    @Test
    @DisplayName("Should check if task template exists")
    void shouldCheckIfTaskTemplateExists() {
        // When
        boolean exists = taskTemplateRepository.existsById(mealTemplate1.getId());
        boolean notExists = taskTemplateRepository.existsById(999L);

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should delete all task templates")
    void shouldDeleteAllTaskTemplates() {
        // When
        taskTemplateRepository.deleteAll();
        entityManager.flush();

        // Then
        assertThat(taskTemplateRepository.findAll()).isEmpty();
        assertThat(taskTemplateRepository.count()).isEqualTo(0);
    }
} 