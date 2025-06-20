package com.EggTart.dyst.EggTart.service;

import com.EggTart.dyst.EggTart.dto.request.OnboardingDto;
import com.EggTart.dyst.EggTart.exception.EntityNotFoundException;
import com.EggTart.dyst.EggTart.model.entity.DogProfile;
import com.EggTart.dyst.EggTart.model.entity.TaskTemplate;
import com.EggTart.dyst.EggTart.model.enums.TaskType;
import com.EggTart.dyst.EggTart.repository.DogRepository;
import com.EggTart.dyst.EggTart.repository.TaskTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Dog Service Tests")
class DogServiceTest {

    @Mock
    private DogRepository dogRepository;

    @Mock
    private TaskTemplateRepository taskTemplateRepository;

    @InjectMocks
    private DogService dogService;

    private DogProfile dogProfile;
    private OnboardingDto onboardingDto;
    private List<TaskTemplate> taskTemplates;

    @BeforeEach
    void setUp() {
        // Setup test data
        dogProfile = DogProfile.builder()
            .id(1L)
            .name("Buddy")
            .ageMo(24)
            .weight(new BigDecimal("15.5"))
            .build();

        onboardingDto = OnboardingDto.builder()
            .dogName("Buddy")
            .ageMo(24)
            .weight(new BigDecimal("15.5"))
            .mealTimes(Arrays.asList(
                LocalTime.of(8, 0),
                LocalTime.of(18, 0)
            ))
            .walkTimes(Arrays.asList(
                LocalTime.of(7, 0),
                LocalTime.of(17, 0)
            ))
            .drinkTimes(Arrays.asList(
                LocalTime.of(9, 0),
                LocalTime.of(15, 0)
            ))
            .build();

        taskTemplates = Arrays.asList(
            TaskTemplate.builder()
                .id(1L)
                .dogId(1L)
                .type(TaskType.MEAL)
                .defaultTime(LocalTime.of(8, 0))
                .build(),
            TaskTemplate.builder()
                .id(2L)
                .dogId(1L)
                .type(TaskType.WALK)
                .defaultTime(LocalTime.of(7, 0))
                .build()
        );
    }

    @Test
    @DisplayName("Should get dog profile successfully")
    void shouldGetDogProfile() {
        // Given
        Long dogId = 1L;
        when(dogRepository.findById(dogId)).thenReturn(Optional.of(dogProfile));

        // When
        DogProfile result = dogService.getDogProfile(dogId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Buddy");
        assertThat(result.getAgeMo()).isEqualTo(24);
        assertThat(result.getWeight()).isEqualTo(new BigDecimal("15.5"));

        verify(dogRepository).findById(dogId);
    }

    @Test
    @DisplayName("Should throw exception when dog profile not found")
    void shouldThrowExceptionWhenDogProfileNotFound() {
        // Given
        Long dogId = 999L;
        when(dogRepository.findById(dogId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> dogService.getDogProfile(dogId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Dog not found with ID: 999");

        verify(dogRepository).findById(dogId);
    }

    @Test
    @DisplayName("Should create dog profile successfully")
    void shouldCreateDogProfile() {
        // Given
        when(dogRepository.save(any(DogProfile.class))).thenReturn(dogProfile);
        when(taskTemplateRepository.save(any(TaskTemplate.class))).thenReturn(new TaskTemplate());

        // When
        DogProfile result = dogService.createDogProfile(onboardingDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Buddy");

        verify(dogRepository).save(any(DogProfile.class));
        // Verify task templates were created (2 meals + 2 walks + 2 drinks = 6 templates)
        verify(taskTemplateRepository, times(6)).save(any(TaskTemplate.class));
    }

    @Test
    @DisplayName("Should create dog profile with only meal times")
    void shouldCreateDogProfileWithOnlyMealTimes() {
        // Given
        OnboardingDto mealOnlyDto = OnboardingDto.builder()
            .dogName("Buddy")
            .ageMo(24)
            .weight(new BigDecimal("15.5"))
            .mealTimes(Arrays.asList(LocalTime.of(8, 0), LocalTime.of(18, 0)))
            .build();

        when(dogRepository.save(any(DogProfile.class))).thenReturn(dogProfile);
        when(taskTemplateRepository.save(any(TaskTemplate.class))).thenReturn(new TaskTemplate());

        // When
        DogProfile result = dogService.createDogProfile(mealOnlyDto);

        // Then
        assertThat(result).isNotNull();
        verify(dogRepository).save(any(DogProfile.class));
        // Verify only meal templates were created (2 meals)
        verify(taskTemplateRepository, times(2)).save(any(TaskTemplate.class));
    }

    @Test
    @DisplayName("Should create dog profile with no task times")
    void shouldCreateDogProfileWithNoTaskTimes() {
        // Given
        OnboardingDto noTasksDto = OnboardingDto.builder()
            .dogName("Buddy")
            .ageMo(24)
            .weight(new BigDecimal("15.5"))
            .build();

        when(dogRepository.save(any(DogProfile.class))).thenReturn(dogProfile);

        // When
        DogProfile result = dogService.createDogProfile(noTasksDto);

        // Then
        assertThat(result).isNotNull();
        verify(dogRepository).save(any(DogProfile.class));
        // Verify no task templates were created
        verify(taskTemplateRepository, never()).save(any(TaskTemplate.class));
    }

    @Test
    @DisplayName("Should update dog profile successfully")
    void shouldUpdateDogProfile() {
        // Given
        Long dogId = 1L;
        OnboardingDto updateDto = OnboardingDto.builder()
            .dogName("Buddy Updated")
            .ageMo(30)
            .weight(new BigDecimal("16.0"))
            .mealTimes(Arrays.asList(LocalTime.of(9, 0)))
            .build();

        when(dogRepository.findById(dogId)).thenReturn(Optional.of(dogProfile));
        when(dogRepository.save(any(DogProfile.class))).thenReturn(dogProfile);
        when(taskTemplateRepository.findByDogIdOrderByDefaultTime(dogId)).thenReturn(taskTemplates);
        when(taskTemplateRepository.save(any(TaskTemplate.class))).thenReturn(new TaskTemplate());

        // When
        DogProfile result = dogService.updateDogProfile(dogId, updateDto);

        // Then
        assertThat(result).isNotNull();
        verify(dogRepository).findById(dogId);
        verify(dogRepository).save(any(DogProfile.class));
        verify(taskTemplateRepository).findByDogIdOrderByDefaultTime(dogId);
        verify(taskTemplateRepository).deleteAll(taskTemplates);
        // Verify new task template was created (1 meal)
        verify(taskTemplateRepository, times(1)).save(any(TaskTemplate.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent dog profile")
    void shouldThrowExceptionWhenUpdatingNonExistentDogProfile() {
        // Given
        Long dogId = 999L;
        when(dogRepository.findById(dogId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> dogService.updateDogProfile(dogId, onboardingDto))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Dog not found with ID: 999");

        verify(dogRepository).findById(dogId);
        verify(dogRepository, never()).save(any());
        verify(taskTemplateRepository, never()).findByDogIdOrderByDefaultTime(any());
    }

    @Test
    @DisplayName("Should get task templates successfully")
    void shouldGetTaskTemplates() {
        // Given
        Long dogId = 1L;
        when(taskTemplateRepository.findByDogIdOrderByDefaultTime(dogId)).thenReturn(taskTemplates);

        // When
        List<TaskTemplate> result = dogService.getTaskTemplates(dogId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getType()).isEqualTo(TaskType.MEAL);
        assertThat(result.get(1).getType()).isEqualTo(TaskType.WALK);

        verify(taskTemplateRepository).findByDogIdOrderByDefaultTime(dogId);
    }

    @Test
    @DisplayName("Should return empty list when no task templates exist")
    void shouldReturnEmptyListWhenNoTaskTemplatesExist() {
        // Given
        Long dogId = 1L;
        when(taskTemplateRepository.findByDogIdOrderByDefaultTime(dogId)).thenReturn(Arrays.asList());

        // When
        List<TaskTemplate> result = dogService.getTaskTemplates(dogId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(taskTemplateRepository).findByDogIdOrderByDefaultTime(dogId);
    }
} 