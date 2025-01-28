package com.backendContextAssignment1.plantSwap_postgres.Services;

import com.backendContextAssignment1.plantSwap_postgres.models.Plant;
import com.backendContextAssignment1.plantSwap_postgres.models.User;
import com.backendContextAssignment1.plantSwap_postgres.models.supportClasses.PlantAvailabilityStatusEnum;
import com.backendContextAssignment1.plantSwap_postgres.repositories.PlantRepository;
import com.backendContextAssignment1.plantSwap_postgres.repositories.TransactionRepository;
import com.backendContextAssignment1.plantSwap_postgres.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PlantRepository plantRepository;
    private final TransactionRepository transactionRepository;

    public UserService(UserRepository userRepository, PlantRepository plantRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.plantRepository = plantRepository;
        this.transactionRepository = transactionRepository;
    }

    public User createUser(User user) {
        //sets createdAt and updatedAt timestamps, overriding any potential user input from RequestBody for these variables
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(null);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return validateUserIdAndReturnUser(id);
    }

    public List<Plant> getPlantsByUserId(Long id) {
        return plantRepository.findByUser(validateUserIdAndReturnUser(id));
    }

    public void deleteUserById(Long id) {
        User user = validateUserIdAndReturnUser(id);
        if (!plantRepository.findByUserAndAvailabilityStatus(user, PlantAvailabilityStatusEnum.RESERVED).isEmpty()) {
            throw new UnsupportedOperationException("users with plants with pending transactions cannot be deleted");
        }
        if (!transactionRepository.findByBuyer(user).isEmpty()) {
            throw new UnsupportedOperationException("users with pending transactions cannot be deleted");
        }
        userRepository.deleteById(id);
    }

    public User updateUser(Long id, User newUser) {
        User existingUser = validateUserIdAndReturnUser(id);

        existingUser.setUsername(newUser.getUsername());
        existingUser.setPassword(newUser.getPassword());
        existingUser.setEmail(newUser.getEmail());
        existingUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(existingUser);
    }

    /*validates that a user exists in database, either casts exception or returns a User
    (findByID() returns Optional<User>, but can be saved as User if expression includes casting exceptions if no User is returned by repository*/
    private User validateUserIdAndReturnUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Id does not correspond to any existing user"));
    }

}
