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
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final PlantRepository plantRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, PlantRepository plantRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.plantRepository = plantRepository;
    }

    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getUpdatedAt() != null) {
            throw new IllegalArgumentException("cannot input value for update_at for a new transaction");
        }

        if (!userRepository.existsById(transaction.getBuyer().getId())) {
            throw new IllegalArgumentException("buyer_id does not correspond to any existing user");
        }
        if (!plantRepository.existsById(transaction.getPlant().getId())) {
            throw new IllegalArgumentException("plant_id does not correspond to any existing user");
        }

        //had to make a plant to not get a null value for the "getUser()" from the plant. Some issue with it being 2 references away.
        Plant plant = plantRepository.getReferenceById(transaction.getPlant().getId());
        if (plant.getAvailabilityStatus() != PlantAvailabilityStatusEnum.AVAILABLE) {
            throw new IllegalArgumentException("plant is reserved or not available");
        }

        if (transaction.getBuyer().getId() == plant.getUser().getId()) {
           throw new IllegalArgumentException("buyer_id cannot be the same as user_id for the owner of the plant for the transaction");
        }
        if (plant.getPrice() == null && transaction.getSwapOffer() == null || plant.getPrice() != null && transaction.getSwapOffer() != null) {
            throw new IllegalArgumentException("transaction for plants with swap_conditions must have a swap offer, transaction for plants with a price must NOT have a swap offer");
        }

        if (plant.getPrice() == null) {
            transaction.setStatus(TransactionStatusEnum.SWAP_PENDING);
            plant.setAvailabilityStatus(PlantAvailabilityStatusEnum.RESERVED);
        } else {
            transaction.setStatus(TransactionStatusEnum.ACCEPTED);
            plant.setAvailabilityStatus(PlantAvailabilityStatusEnum.NOT_AVAILABLE);
        }
        plant.setUpdatedAt(LocalDateTime.now());
        plantRepository.save(plant);

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        validateTransactionId(id);
        return transactionRepository.findById(id);
    }

    public List<Transaction> getTransactionsByUserId(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Id does not correspond to any existing user");
        }
        return transactionRepository.findByBuyer(userRepository.findById(id));
    }

    public void deleteTransactionById(Long id) {
        validateTransactionId(id);
        transactionRepository.deleteById(id);
    }

    private void validateTransactionId(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new IllegalArgumentException("Id does not correspond to any existing transaction");
        }
    }
}
