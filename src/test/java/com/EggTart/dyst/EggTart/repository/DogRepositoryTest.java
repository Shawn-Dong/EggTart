package com.EggTart.dyst.EggTart.repository;

import com.EggTart.dyst.EggTart.model.entity.DogProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Dog Repository Tests")
class DogRepositoryTest {

    private final DogRepository dogRepository;
    private final TestEntityManager entityManager;

    private DogProfile dog1;
    private DogProfile dog2;

    // Constructor injection - @Autowired is required for @DataJpaTest
    DogRepositoryTest(
        @Autowired DogRepository dogRepository,
        @Autowired TestEntityManager entityManager
    ) {
        this.dogRepository = dogRepository;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        // Clear database
        dogRepository.deleteAll();
        entityManager.flush();

        // Create test data
        dog1 = DogProfile.builder()
            .name("Buddy")
            .ageMo(24)
            .weight(new BigDecimal("15.5"))
            .puppyFlag(false)
            .build();

        dog2 = DogProfile.builder()
            .name("Max")
            .ageMo(6)
            .weight(new BigDecimal("8.2"))
            .puppyFlag(true)
            .build();

        // Save test data
        dog1 = dogRepository.save(dog1);
        dog2 = dogRepository.save(dog2);
    }

    @Test
    @DisplayName("Should save and retrieve dog profile")
    void shouldSaveAndRetrieveDogProfile() {
        // Given
        DogProfile newDog = DogProfile.builder()
            .name("Rex")
            .ageMo(36)
            .weight(new BigDecimal("20.0"))
            .puppyFlag(false)
            .build();

        // When
        DogProfile savedDog = dogRepository.save(newDog);
        Optional<DogProfile> retrievedDog = dogRepository.findById(savedDog.getId());

        // Then
        assertThat(retrievedDog).isPresent();
        assertThat(retrievedDog.get().getName()).isEqualTo("Rex");
        assertThat(retrievedDog.get().getAgeMo()).isEqualTo(36);
        assertThat(retrievedDog.get().getWeight()).isEqualTo(new BigDecimal("20.0"));
        assertThat(retrievedDog.get().getPuppyFlag()).isFalse();
    }

    @Test
    @DisplayName("Should find dog profile by ID")
    void shouldFindDogProfileById() {
        // When
        Optional<DogProfile> foundDog = dogRepository.findById(dog1.getId());

        // Then
        assertThat(foundDog).isPresent();
        assertThat(foundDog.get().getName()).isEqualTo("Buddy");
        assertThat(foundDog.get().getAgeMo()).isEqualTo(24);
    }

    @Test
    @DisplayName("Should return empty when dog profile not found")
    void shouldReturnEmptyWhenDogProfileNotFound() {
        // When
        Optional<DogProfile> foundDog = dogRepository.findById(999L);

        // Then
        assertThat(foundDog).isEmpty();
    }

    @Test
    @DisplayName("Should find all dog profiles")
    void shouldFindAllDogProfiles() {
        // When
        List<DogProfile> allDogs = dogRepository.findAll();

        // Then
        assertThat(allDogs).hasSize(2);
        assertThat(allDogs).extracting("name").containsExactlyInAnyOrder("Buddy", "Max");
    }

    @Test
    @DisplayName("Should update dog profile")
    void shouldUpdateDogProfile() {
        // Given
        String newName = "Buddy Updated";
        Integer newAge = 30;
        BigDecimal newWeight = new BigDecimal("16.0");

        // When
        dog1.setName(newName);
        dog1.setAgeMo(newAge);
        dog1.setWeight(newWeight);
        DogProfile updatedDog = dogRepository.save(dog1);

        // Then
        assertThat(updatedDog.getName()).isEqualTo(newName);
        assertThat(updatedDog.getAgeMo()).isEqualTo(newAge);
        assertThat(updatedDog.getWeight()).isEqualTo(newWeight);

        // Verify in database
        Optional<DogProfile> retrievedDog = dogRepository.findById(dog1.getId());
        assertThat(retrievedDog).isPresent();
        assertThat(retrievedDog.get().getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Should delete dog profile")
    void shouldDeleteDogProfile() {
        // Given
        Long dogId = dog1.getId();

        // When
        dogRepository.deleteById(dogId);
        entityManager.flush();

        // Then
        assertThat(dogRepository.findById(dogId)).isEmpty();
        assertThat(dogRepository.findAll()).hasSize(1); // Only dog2 remains
    }

    @Test
    @DisplayName("Should handle puppy flag automatically")
    void shouldHandlePuppyFlagAutomatically() {
        // Given
        DogProfile puppyDog = DogProfile.builder()
            .name("Puppy")
            .ageMo(6) // Less than 12 months
            .weight(new BigDecimal("5.0"))
            .build();

        DogProfile adultDog = DogProfile.builder()
            .name("Adult")
            .ageMo(24) // More than 12 months
            .weight(new BigDecimal("15.0"))
            .build();

        // When
        DogProfile savedPuppy = dogRepository.save(puppyDog);
        DogProfile savedAdult = dogRepository.save(adultDog);

        // Then
        assertThat(savedPuppy.getPuppyFlag()).isTrue(); // Auto-set to true for age <= 12
        assertThat(savedAdult.getPuppyFlag()).isFalse(); // Auto-set to false for age > 12
    }

    @Test
    @DisplayName("Should count total dog profiles")
    void shouldCountTotalDogProfiles() {
        // When
        long count = dogRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should check if dog profile exists")
    void shouldCheckIfDogProfileExists() {
        // When
        boolean exists = dogRepository.existsById(dog1.getId());
        boolean notExists = dogRepository.existsById(999L);

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should delete all dog profiles")
    void shouldDeleteAllDogProfiles() {
        // When
        dogRepository.deleteAll();
        entityManager.flush();

        // Then
        assertThat(dogRepository.findAll()).isEmpty();
        assertThat(dogRepository.count()).isEqualTo(0);
    }
} 