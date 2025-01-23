package com.backendContextAssignment1.plantSwap_postgres.Services;

import com.backendContextAssignment1.plantSwap_postgres.models.Plant;
import com.backendContextAssignment1.plantSwap_postgres.models.Transaction;
import com.backendContextAssignment1.plantSwap_postgres.models.supportClasses.PlantAvailabilityStatusEnum;
import com.backendContextAssignment1.plantSwap_postgres.models.supportClasses.TransactionStatusEnum;
import com.backendContextAssignment1.plantSwap_postgres.repositories.PlantRepository;
import com.backendContextAssignment1.plantSwap_postgres.repositories.TransactionRepository;
import com.backendContextAssignment1.plantSwap_postgres.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlantService {
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public PlantService(PlantRepository plantRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.plantRepository = plantRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public Plant createPlant(Plant plant) {
        if (plant.getUpdatedAt() != null) {
            throw new IllegalArgumentException("cannot input value for update_at for a new plant");
        }
        if (!userRepository.existsById(plant.getUser().getId())) {
            throw new IllegalArgumentException("user_id does not correspond to any existing user");
        }
        if (plantRepository.findByUserAndAvailabilityStatus(plant.getUser(), PlantAvailabilityStatusEnum.AVAILABLE).size() >= 10) {
            throw new IllegalArgumentException("user already has 10 available plants in database no new plants can be added for this user");
        }
        validateHasEitherSwapOrPriceNotBoth(plant);
        return plantRepository.save(plant);
    }

    public List<Plant> getAllPlants() {
        return plantRepository.findAll();
    }

    public Plant getPlantById(Long id) {
        return validatePlantIdAndReturnPlant(id);
    }

    public List<Plant> getAvailablePlants() {
        return plantRepository.findByAvailabilityStatus(PlantAvailabilityStatusEnum.AVAILABLE);
    }

    public void deletePlantById(Long id) {
        validatePlantIdAndReturnPlant(id);
        plantRepository.deleteById(id);
    }

    public Plant updatePlant(Long id, Plant newPlant) {
        Plant existingPlant = validatePlantIdAndReturnPlant(id);

        for (Transaction transaction : transactionRepository.findByPlant(existingPlant)) {
            if (transaction.getStatus() != TransactionStatusEnum.SWAP_REJECTED) {
                throw new IllegalArgumentException("plant cannot be updated due to having an accepted or pending transaction");
            }

        }

        if (newPlant.getUser() != existingPlant.getUser()) {
            throw new IllegalArgumentException("user_id cannot be changed");
        }
        validateHasEitherSwapOrPriceNotBoth(newPlant);

        existingPlant.setCommonName(newPlant.getCommonName());
        existingPlant.setPlantFamily(newPlant.getPlantFamily());
        existingPlant.setPlantGenus(newPlant.getPlantGenus());
        existingPlant.setPlantStage(newPlant.getPlantStage());
        existingPlant.setPlantSize(newPlant.getPlantSize());
        existingPlant.setCareDifficulty(newPlant.getCareDifficulty());
        existingPlant.setLightRequirement(newPlant.getLightRequirement());
        existingPlant.setWaterRequirement(newPlant.getWaterRequirement());
        existingPlant.setPrice(newPlant.getPrice());
        existingPlant.setSwapConditions(newPlant.getSwapConditions());
        existingPlant.setImageURL(newPlant.getImageURL());
        existingPlant.setDescription(newPlant.getDescription());
        existingPlant.setUpdatedAt(LocalDateTime.now());

        return plantRepository.save(existingPlant);
    }

    private Plant validatePlantIdAndReturnPlant(Long id) {
        return plantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id does not correspond to any existing plant"));

    }

    private void validateHasEitherSwapOrPriceNotBoth(Plant plant) {
        if ((plant.getPrice() == null && plant.getSwapConditions() == null) || (plant.getPrice() != null && plant.getSwapConditions() != null)) {
            throw new IllegalArgumentException("plant must either have price or swap_offer but not both");
        }
    }
}
