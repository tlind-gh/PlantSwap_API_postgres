package com.backendContextAssignment1.plantSwap_postgres.Services;

import com.backendContextAssignment1.plantSwap_postgres.models.User;
import com.backendContextAssignment1.plantSwap_postgres.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
