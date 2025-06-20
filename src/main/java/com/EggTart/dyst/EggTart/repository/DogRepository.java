package com.EggTart.dyst.EggTart.repository;

import com.EggTart.dyst.EggTart.model.entity.DogProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DogRepository extends JpaRepository<DogProfile, Long> {
    // Basic CRUD operations are provided by JpaRepository
} 