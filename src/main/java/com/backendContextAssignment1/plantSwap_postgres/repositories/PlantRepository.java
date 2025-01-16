package com.backendContextAssignment1.plantSwap_postgres.repositories;

import com.backendContextAssignment1.plantSwap_postgres.models.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantRepository  extends JpaRepository<Plant, Long> {
}
