package com.backendContextAssignment1.plantSwap_postgres.Services;

import com.backendContextAssignment1.plantSwap_postgres.models.Transaction;
import com.backendContextAssignment1.plantSwap_postgres.repositories.PlantRepository;
import com.backendContextAssignment1.plantSwap_postgres.repositories.TransactionRepository;
import com.backendContextAssignment1.plantSwap_postgres.repositories.UserRepository;
import org.springframework.stereotype.Service;

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
        this. plantRepository = plantRepository;
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
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new IllegalArgumentException("Id does not correspond to any existing transaction");
        }
        return transactionRepository.findById(id);
    }

    public void deleteTransactionById(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new IllegalArgumentException("Id does not correspond to any existing transaction");
        }
        transactionRepository.deleteById(id);
    }
}
