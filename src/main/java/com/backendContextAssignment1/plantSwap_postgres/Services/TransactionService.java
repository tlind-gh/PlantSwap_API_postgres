package com.backendContextAssignment1.plantSwap_postgres.Services;

import com.backendContextAssignment1.plantSwap_postgres.models.Transaction;
import com.backendContextAssignment1.plantSwap_postgres.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getUpdatedAt() != null) {
            throw new IllegalArgumentException("cannot input value for update_at for a new user");
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
