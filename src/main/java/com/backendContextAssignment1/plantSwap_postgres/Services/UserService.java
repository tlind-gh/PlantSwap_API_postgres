package com.backendContextAssignment1.plantSwap_postgres.Services;

import com.backendContextAssignment1.plantSwap_postgres.models.Plant;
import com.backendContextAssignment1.plantSwap_postgres.models.User;
import com.backendContextAssignment1.plantSwap_postgres.repositories.PlantRepository;
import com.backendContextAssignment1.plantSwap_postgres.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PlantRepository plantRepository;

    public UserService(UserRepository userRepository, PlantRepository plantRepository) {
        this.userRepository = userRepository;
        this.plantRepository = plantRepository;
    }

    public User createUser(User user) {
        if (user.getUpdatedAt() != null) {
            throw new IllegalArgumentException("updatedAt must be null for new user");
        }
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
        validateUserIdAndReturnUser(id);
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

    private User validateUserIdAndReturnUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Id does not correspond to any existing user"));
    }

}
