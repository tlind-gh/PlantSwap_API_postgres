package com.backendContextAssignment1.plantSwap_postgres.Services;

import com.backendContextAssignment1.plantSwap_postgres.models.Plant;
import com.backendContextAssignment1.plantSwap_postgres.repositories.PlantRepository;
import com.backendContextAssignment1.plantSwap_postgres.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlantService {
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;

    public PlantService(PlantRepository plantRepository, UserRepository userRepository) {
        this.plantRepository = plantRepository;
        this.userRepository = userRepository;
    }

    public Plant createPlant(Plant plant) {
        if (plant.getUpdatedAt() != null) {
            throw new IllegalArgumentException("cannot input value for update_at for a new plant");
        }
        if ((plant.getPrice() == null && plant.getSwapConditions() == null) || (plant.getPrice() != null && plant.getSwapConditions() != null)) {
            throw new IllegalArgumentException("plant must either have price or swapoffer but not both");
        }
        if (!userRepository.existsById(plant.getUser().getId())) {
            throw new IllegalArgumentException("user_id does not correspond to any existing user");
        }
        return plantRepository.save(plant);
    }

    public List<Plant> getAllPlants() {
        return plantRepository.findAll();
    }

    public Optional<Plant> getPlantById(Long id) {
        if (!plantRepository.existsById(id)) {
            throw new IllegalArgumentException("Id does not correspond to any existing plant");
        }
        return plantRepository.findById(id);
    }

    public void deletePlantById(Long id) {
        if (!plantRepository.existsById(id)) {
            throw new IllegalArgumentException("Id does not correspond to any existing plant");
        }
        plantRepository.deleteById(id);
    }
}
