package com.backendContextAssignment1.plantSwap_postgres.repositories;

import com.backendContextAssignment1.plantSwap_postgres.models.Transaction;
import com.backendContextAssignment1.plantSwap_postgres.models.User;
import com.backendContextAssignment1.plantSwap_postgres.models.supportClasses.TransactionStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByBuyer(User user);
    List<Transaction> findByBuyerAndStatus(User user, TransactionStatusEnum status);
}
