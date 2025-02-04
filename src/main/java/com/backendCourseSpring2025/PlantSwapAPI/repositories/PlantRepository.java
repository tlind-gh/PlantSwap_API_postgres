package com.backendCourseSpring2025.PlantSwapAPI.repositories;

import com.backendCourseSpring2025.PlantSwapAPI.models.Plant;
import com.backendCourseSpring2025.PlantSwapAPI.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantRepository  extends JpaRepository<Plant, Long> {
    List<Plant> findByUser(User user);
}
