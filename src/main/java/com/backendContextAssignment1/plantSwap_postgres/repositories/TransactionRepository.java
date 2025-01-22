package com.backendContextAssignment1.plantSwap_postgres.repositories;

import com.backendContextAssignment1.plantSwap_postgres.models.Transaction;
import com.backendContextAssignment1.plantSwap_postgres.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByBuyer(Optional<User> user);
}
