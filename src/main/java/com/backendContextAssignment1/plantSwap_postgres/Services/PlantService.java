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
        //check that user exists in database
        if (!userRepository.existsById(plant.getUser().getId())) {
            throw new NoSuchElementException("user id does not correspond to any existing user");
        }
        //check if user already had the maximum allowed nr of available plants
        if (plantRepository.findByUserAndAvailabilityStatus(plant.getUser(), PlantAvailabilityStatusEnum.AVAILABLE).size() >= 10) {
            throw new IllegalArgumentException("user already has 10 available plants in database, no new plants can be added for this user");
        }

        //check that exactly one of two fields is null: "price" and "swapConditions" (plant must either be for sale or for swap, but not both)
        validateHasEitherSwapOrPriceNotBoth(plant);

        //sets createdAt and updatedAt timestamps (overriding any potential user input from RequestBody for these variables)
        plant.setCreatedAt(LocalDateTime.now());
        plant.setUpdatedAt(null);

        return plantRepository.save(plant);
    }

    //get all plants in database
    public List<Plant> getAllPlants() {
        return plantRepository.findAll();
    }

    //get a plant by id
    public Plant getPlantById(Long id) {
        return validatePlantIdAndReturnPlant(id);
    }

    //get all plants with availabilityStatus = AVAILABLE
    public List<Plant> getAvailablePlants() {
        return plantRepository.findByAvailabilityStatus(PlantAvailabilityStatusEnum.AVAILABLE);
    }

    /*delete a plant using id, plants with availabilityStatus = RESERVED (i.e., with a pending transactions)
    NB! delete type = cascade: also deletes the transactions for that plant.*/
    public void deletePlantById(Long id) {
        if (validatePlantIdAndReturnPlant(id).getAvailabilityStatus() == PlantAvailabilityStatusEnum.RESERVED) {
            throw new UnsupportedOperationException("plants with pending transactions cannot be deleted");
        }
        plantRepository.deleteById(id);
    }

    //will update ALL fields for a plant in accordance with a new plant (from RequestBody through PlantController)
    public Plant updatePlant(Long id, Plant newPlant) {
        Plant existingPlant = validatePlantIdAndReturnPlant(id);
        /*check that plant is not reserved or already sold (plants with pending or accepted transactions should not be changed
        since their data should remain accurate to the closed or ongoing transactions*/
        if (existingPlant.getAvailabilityStatus() != PlantAvailabilityStatusEnum.AVAILABLE) {
            throw new IllegalArgumentException("plants with accepted or pending transactions cannot be updated");
        }

        //plant status is only updated via transactions
        if (newPlant.getAvailabilityStatus() != existingPlant.getAvailabilityStatus()) {
            throw new IllegalArgumentException("plants status cannot be updated");
        }

        //plants listing cannot change owner (plant can be swapped or sold, but the plant listing does not transfer owner)
        if (newPlant.getUser().getId() != existingPlant.getUser().getId()) {
            throw new IllegalArgumentException("user id cannot be updated");
        }

        //validate that the updated plant has price OR swapConditions
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
