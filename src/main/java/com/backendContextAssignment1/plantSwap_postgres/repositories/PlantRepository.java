package com.backendContextAssignment1.plantSwap_postgres.repositories;

import com.backendContextAssignment1.plantSwap_postgres.models.Plant;
import com.backendContextAssignment1.plantSwap_postgres.models.User;
import com.backendContextAssignment1.plantSwap_postgres.models.supportClasses.PlantAvailabilityStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantRepository  extends JpaRepository<Plant, Long> {

    List<Plant> findByUser(User user);
    List<Plant> findByAvailabilityStatus(PlantAvailabilityStatusEnum availabilityStatus);
    List<Plant> findByUserAndAvailabilityStatus(User user, PlantAvailabilityStatusEnum availabilityStatus);
}
