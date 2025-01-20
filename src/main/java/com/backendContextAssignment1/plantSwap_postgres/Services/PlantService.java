package com.backendContextAssignment1.plantSwap_postgres.Services;

import com.backendContextAssignment1.plantSwap_postgres.models.Plant;
import com.backendContextAssignment1.plantSwap_postgres.repositories.PlantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantService {
    private final PlantRepository plantRepository;

    public PlantService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    public Plant createPlant(Plant plant) {
        if (plant.getUpdatedAt() != null) {
            throw new IllegalArgumentException("cannot input value for update_at for a new plant");
        }
        return plantRepository.save(plant);
    }

    public List<Plant> getAllPlants() {
        return plantRepository.findAll();
    }
}
