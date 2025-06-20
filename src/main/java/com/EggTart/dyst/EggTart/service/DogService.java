package com.EggTart.dyst.EggTart.service;

import com.EggTart.dyst.EggTart.dto.request.OnboardingDto;
import com.EggTart.dyst.EggTart.exception.EntityNotFoundException;
import com.EggTart.dyst.EggTart.model.entity.DogProfile;
import com.EggTart.dyst.EggTart.model.entity.TaskTemplate;
import com.EggTart.dyst.EggTart.model.enums.TaskType;
import com.EggTart.dyst.EggTart.repository.DogRepository;
import com.EggTart.dyst.EggTart.repository.TaskTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class DogService {
    
    private final DogRepository dogRepository;
    private final TaskTemplateRepository taskTemplateRepository;
    
    @Transactional(readOnly = true)
    public DogProfile getDogProfile(Long dogId) {
        log.debug("Fetching dog profile for ID: {}", dogId);
        
        return dogRepository.findById(dogId)
            .orElseThrow(() -> new EntityNotFoundException("Dog", dogId));
    }
    
    @Transactional
    public DogProfile createDogProfile(OnboardingDto onboardingDto) {
        log.info("Creating dog profile for: {}", onboardingDto.getDogName());
        
        // Create dog profile
        DogProfile dogProfile = DogProfile.builder()
            .name(onboardingDto.getDogName())
            .ageMo(onboardingDto.getAgeMo())
            .weight(onboardingDto.getWeight())
            .build();
        
        DogProfile savedDog = dogRepository.save(dogProfile);
        
        // Create task templates
        createTaskTemplates(savedDog.getId(), onboardingDto);
        
        log.info("Dog profile created successfully with ID: {}", savedDog.getId());
        return savedDog;
    }
    
    @Transactional
    public DogProfile updateDogProfile(Long dogId, OnboardingDto onboardingDto) {
        log.info("Updating dog profile for ID: {}", dogId);
        
        DogProfile existingDog = getDogProfile(dogId);
        
        // Update basic info
        existingDog.setName(onboardingDto.getDogName());
        existingDog.setAgeMo(onboardingDto.getAgeMo());
        existingDog.setWeight(onboardingDto.getWeight());
        
        DogProfile savedDog = dogRepository.save(existingDog);
        
        // Delete existing task templates and create new ones
        List<TaskTemplate> existingTemplates = taskTemplateRepository
            .findByDogIdOrderByDefaultTime(dogId);
        taskTemplateRepository.deleteAll(existingTemplates);
        
        createTaskTemplates(dogId, onboardingDto);
        
        log.info("Dog profile updated successfully");
        return savedDog;
    }
    
    private void createTaskTemplates(Long dogId, OnboardingDto onboardingDto) {
        // Create meal time templates
        if (onboardingDto.getMealTimes() != null) {
            for (java.time.LocalTime mealTime : onboardingDto.getMealTimes()) {
                TaskTemplate mealTemplate = TaskTemplate.builder()
                    .dogId(dogId)
                    .type(TaskType.MEAL)
                    .defaultTime(mealTime)
                    .build();
                taskTemplateRepository.save(mealTemplate);
            }
        }
        
        // Create walk time templates
        if (onboardingDto.getWalkTimes() != null) {
            for (java.time.LocalTime walkTime : onboardingDto.getWalkTimes()) {
                TaskTemplate walkTemplate = TaskTemplate.builder()
                    .dogId(dogId)
                    .type(TaskType.WALK)
                    .defaultTime(walkTime)
                    .build();
                taskTemplateRepository.save(walkTemplate);
            }
        }
        
        // Create drink time templates
        if (onboardingDto.getDrinkTimes() != null) {
            for (java.time.LocalTime drinkTime : onboardingDto.getDrinkTimes()) {
                TaskTemplate drinkTemplate = TaskTemplate.builder()
                    .dogId(dogId)
                    .type(TaskType.DRINK)
                    .defaultTime(drinkTime)
                    .build();
                taskTemplateRepository.save(drinkTemplate);
            }
        }
        
        log.debug("Created task templates for dog ID: {}", dogId);
    }
    
    @Transactional(readOnly = true)
    public List<TaskTemplate> getTaskTemplates(Long dogId) {
        return taskTemplateRepository.findByDogIdOrderByDefaultTime(dogId);
    }
} 