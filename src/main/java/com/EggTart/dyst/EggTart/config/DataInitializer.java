package com.EggTart.dyst.EggTart.config;

import com.EggTart.dyst.EggTart.model.entity.DogProfile;
import com.EggTart.dyst.EggTart.model.entity.TaskInstance;
import com.EggTart.dyst.EggTart.model.entity.TaskTemplate;
import com.EggTart.dyst.EggTart.model.enums.TaskStatus;
import com.EggTart.dyst.EggTart.model.enums.TaskType;
import com.EggTart.dyst.EggTart.repository.DogRepository;
import com.EggTart.dyst.EggTart.repository.TaskInstanceRepository;
import com.EggTart.dyst.EggTart.repository.TaskTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final DogRepository dogRepository;
    private final TaskTemplateRepository taskTemplateRepository;
    private final TaskInstanceRepository taskInstanceRepository;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing sample data for EggTart application...");
        
        // Check if data already exists
        if (dogRepository.count() > 0) {
            log.info("Sample data already exists, skipping initialization");
            return;
        }
        
        // Create sample dog profile
        DogProfile eggTart = DogProfile.builder()
            .name("EggTart")
            .ageMo(24)
            .weight(new BigDecimal("15.5"))
            .puppyFlag(false)
            .mealOffsetMinutes(30)
            .drinkOffsetMinutes(15)
            .build();
        
        DogProfile savedDog = dogRepository.save(eggTart);
        log.info("Created dog profile: {}", savedDog.getName());
        
        // Create task templates
        createTaskTemplates(savedDog.getId());
        
        // Create today's task instances
        createTodayTasks(savedDog.getId());
        
        log.info("Sample data initialization completed successfully");
    }
    
    private void createTaskTemplates(Long dogId) {
        // Meal times
        Arrays.asList(
            LocalTime.of(8, 0),   // 8:00 AM
            LocalTime.of(18, 0)   // 6:00 PM
        ).forEach(time -> {
            TaskTemplate template = TaskTemplate.builder()
                .dogId(dogId)
                .type(TaskType.MEAL)
                .defaultTime(time)
                .build();
            taskTemplateRepository.save(template);
        });
        
        // Walk times
        Arrays.asList(
            LocalTime.of(9, 30),  // 9:30 AM
            LocalTime.of(15, 0),  // 3:00 PM
            LocalTime.of(20, 30)  // 8:30 PM
        ).forEach(time -> {
            TaskTemplate template = TaskTemplate.builder()
                .dogId(dogId)
                .type(TaskType.WALK)
                .defaultTime(time)
                .build();
            taskTemplateRepository.save(template);
        });
        
        // Drink times
        Arrays.asList(
            LocalTime.of(12, 0)   // 12:00 PM
        ).forEach(time -> {
            TaskTemplate template = TaskTemplate.builder()
                .dogId(dogId)
                .type(TaskType.DRINK)
                .defaultTime(time)
                .build();
            taskTemplateRepository.save(template);
        });
        
        log.info("Created task templates for dog ID: {}", dogId);
    }
    
    private void createTodayTasks(Long dogId) {
        LocalDate today = LocalDate.now();
        
        // Create meal tasks
        createTaskInstance(dogId, TaskType.MEAL, today.atTime(8, 0));
        createTaskInstance(dogId, TaskType.MEAL, today.atTime(18, 0));
        
        // Create walk tasks
        createTaskInstance(dogId, TaskType.WALK, today.atTime(9, 30));
        createTaskInstance(dogId, TaskType.WALK, today.atTime(15, 0));
        createTaskInstance(dogId, TaskType.WALK, today.atTime(20, 30));
        
        // Create drink task
        createTaskInstance(dogId, TaskType.DRINK, today.atTime(12, 0));
        
        log.info("Created today's tasks for dog ID: {}", dogId);
    }
    
    private void createTaskInstance(Long dogId, TaskType type, LocalDateTime scheduledTime) {
        TaskInstance task = TaskInstance.builder()
            .dogId(dogId)
            .type(type)
            .scheduledTime(scheduledTime)
            .status(TaskStatus.PENDING)
            .build();
        
        taskInstanceRepository.save(task);
    }
} 