package com.backendCourseSpring2025.PlantSwapAPI.repositories;

import com.backendCourseSpring2025.PlantSwapAPI.models.Plant;
import com.backendCourseSpring2025.PlantSwapAPI.models.Transaction;
import com.backendCourseSpring2025.PlantSwapAPI.models.User;
import com.backendCourseSpring2025.PlantSwapAPI.models.supportClasses.TransactionStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByBuyer(User user);
    List<Transaction> findByBuyerAndStatus(User user, TransactionStatusEnum status);
    List<Transaction> findByPlantAndStatus(Plant plant, TransactionStatusEnum status);
}
