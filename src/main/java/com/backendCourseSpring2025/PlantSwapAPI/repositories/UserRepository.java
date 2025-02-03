package com.backendCourseSpring2025.PlantSwapAPI.repositories;

import com.backendCourseSpring2025.PlantSwapAPI.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User, Long> {
}
