package com.backendContextAssignment1.plantSwap_postgres.Services;

import com.backendContextAssignment1.plantSwap_postgres.models.User;
import com.backendContextAssignment1.plantSwap_postgres.repositories.PlantRepository;
import com.backendContextAssignment1.plantSwap_postgres.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
            throw new IllegalArgumentException("cannot input value for update_at for a new user");
        }
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        validateUserId(id);
        return userRepository.findById(id);
    }

    /*
        public List<Plant> getUserPlantsByUserId(Long id) {
            validateUserId(id);
            plantRepository.findAll()
        }
    */
    public void deleteUserById(Long id) {
        validateUserId(id);
        userRepository.deleteById(id);
    }

    private void validateUserId(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Id does not correspond to any existing user");
        }
    }
}
