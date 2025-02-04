package com.backendCourseSpring2025.PlantSwapAPI.Services;

import com.backendCourseSpring2025.PlantSwapAPI.models.Plant;
import com.backendCourseSpring2025.PlantSwapAPI.models.User;
import com.backendCourseSpring2025.PlantSwapAPI.models.supportClasses.TransactionStatusEnum;
import com.backendCourseSpring2025.PlantSwapAPI.repositories.PlantRepository;
import com.backendCourseSpring2025.PlantSwapAPI.repositories.TransactionRepository;
import com.backendCourseSpring2025.PlantSwapAPI.repositories.UserRepository;
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

    public List<User> createMultipleUsers(List<User> userList) {
        for (User user : userList) {
            createUser(user);
        }
        return userList;
    }

    //update user, full body
    public User updateUser(Long id, User newUser) {
        User existingUser = validateUserIdAndReturnUser(id);

        existingUser.setUsername(newUser.getUsername());
        existingUser.setPassword(newUser.getPassword());
        existingUser.setEmail(newUser.getEmail());
        existingUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(existingUser);
    }

    //get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //get single user by id
    public User getUserById(Long id) {
        return validateUserIdAndReturnUser(id);
    }


    /*delete a user using user id
    NB! delete type = cascade: also deletes the plants belonging to the user and the transactions for that plant*/
    public void deleteUserById(Long id) {
        User user = validateUserIdAndReturnUser(id);
        //users with pending or accepted transaction, or plants with pending transactions cannot be deleted.
        for (Plant plant : plantRepository.findByUser(user)) {
            if (!transactionRepository.findByPlantAndStatus(plant, TransactionStatusEnum.SWAP_PENDING).isEmpty()) {
                throw new UnsupportedOperationException("users with plants with pending transactions cannot be deleted");
            }
        }
        //user with pending or accepted transactions cannot be deleted
        if (!transactionRepository.findByBuyerAndStatus(user, TransactionStatusEnum.SWAP_PENDING).isEmpty() || !transactionRepository.findByBuyerAndStatus(user, TransactionStatusEnum.ACCEPTED).isEmpty()) {
            throw new UnsupportedOperationException("users with pending or accepted transactions cannot be deleted");
        }

        userRepository.deleteById(id);
    }

    /*validates that a user exists in database, either casts exception or returns a User
    (findByID() returns Optional<User>, but can be saved as User if expression includes casting exceptions if no User is returned by repository*/
    private User validateUserIdAndReturnUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Id does not correspond to any existing user"));
    }

}
