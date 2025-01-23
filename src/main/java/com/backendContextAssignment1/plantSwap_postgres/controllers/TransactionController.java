package com.backendContextAssignment1.plantSwap_postgres.controllers;

import com.backendContextAssignment1.plantSwap_postgres.Services.TransactionService;
import com.backendContextAssignment1.plantSwap_postgres.models.Transaction;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@Valid @RequestBody Transaction transaction) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(transaction));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getTransactionsByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionById(@PathVariable Long id) {
            transactionService.deleteTransactionById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @Valid @RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, transaction));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<Transaction> acceptTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.acceptTransaction(id));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<Transaction> rejectTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.rejectTransaction(id));
    }

    @PatchMapping("/{id}/swapoffer")
    public ResponseEntity<Transaction> updateSwapOffer(@PathVariable Long id, @RequestParam String swapOffer) {
        return ResponseEntity.ok(transactionService.updateSwapOffer(id, swapOffer));
    }


}
