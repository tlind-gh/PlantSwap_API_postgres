package com.backendContextAssignment1.plantSwap_postgres.Services;

import com.backendContextAssignment1.plantSwap_postgres.models.Plant;
import com.backendContextAssignment1.plantSwap_postgres.models.supportClasses.PlantAvailabilityStatusEnum;
import com.backendContextAssignment1.plantSwap_postgres.repositories.PlantRepository;
import com.backendContextAssignment1.plantSwap_postgres.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PlantService {
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;

    public PlantService(PlantRepository plantRepository, UserRepository userRepository) {
        this.plantRepository = plantRepository;
        this.userRepository = userRepository;
    }

    public Plant createPlant(Plant plant) {
        if (!userRepository.existsById(plant.getUser().getId())) {
            throw new NoSuchElementException("user id does not correspond to any existing user");
        }
        if (plantRepository.findByUserAndAvailabilityStatus(plant.getUser(), PlantAvailabilityStatusEnum.AVAILABLE).size() >= 10) {
            throw new IllegalArgumentException("user already has 10 available plants in database, no new plants can be added for this user");
        }
        validateHasEitherSwapOrPriceNotBoth(plant);

        //sets createdAt and updatedAt timestamps, overriding any potential user input from RequestBody for these variables
        plant.setCreatedAt(LocalDateTime.now());
        plant.setUpdatedAt(null);

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
        if (validatePlantIdAndReturnPlant(id).getAvailabilityStatus() == PlantAvailabilityStatusEnum.RESERVED) {
            throw new UnsupportedOperationException("plants with pending transactions cannot be deleted");
        }
        plantRepository.deleteById(id);
    }

    //will update ALL fields for a plant in accordance with a new plant (from RequestBody through PlantController)
    public Plant updatePlant(Long id, Plant newPlant) {
        Plant existingPlant = validatePlantIdAndReturnPlant(id);
        if (existingPlant.getAvailabilityStatus() != PlantAvailabilityStatusEnum.AVAILABLE) {
            throw new IllegalArgumentException("plants with accepted or pending transactions cannot be updated");
        }

        if (newPlant.getAvailabilityStatus() != existingPlant.getAvailabilityStatus()) {
            throw new IllegalArgumentException("plants status cannot be updated");
        }

        if (newPlant.getUser().getId() != existingPlant.getUser().getId()) {
            throw new IllegalArgumentException("user id cannot be updated");
        }

        validateHasEitherSwapOrPriceNotBoth(newPlant);

        existingPlant.setCommonName(newPlant.getCommonName());
        existingPlant.setPlantFamily(newPlant.getPlantFamily());
        existingPlant.setPlantSpecies(newPlant.getPlantSpecies());
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

    /*validates that a user exists in database, either casts exception or returns a User
    (findByID() returns Optional<User>, but can be saved as User if expression includes casting exceptions if no User is returned by repository*/
    private Plant validatePlantIdAndReturnPlant(Long id) {
        return plantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Id does not correspond to any existing plant"));
    }

    /*validates that a new plant has "null" in exactly 1 of the following two fields: "price" and "swapOffer"*/
    private void validateHasEitherSwapOrPriceNotBoth(Plant plant) {
        if ((plant.getPrice() == null && plant.getSwapConditions() == null) || (plant.getPrice() != null && plant.getSwapConditions() != null)) {
            throw new IllegalArgumentException("plant must either have price or swapOffer but not both");
        }
    }
}
