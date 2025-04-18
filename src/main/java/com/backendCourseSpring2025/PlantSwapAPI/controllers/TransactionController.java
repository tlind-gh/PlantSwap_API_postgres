package com.backendCourseSpring2025.PlantSwapAPI.controllers;

import com.backendCourseSpring2025.PlantSwapAPI.services.TransactionService;
import com.backendCourseSpring2025.PlantSwapAPI.models.Transaction;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//REST controller for the Transaction class. Handles Transactions indirectly via TransactionService class.
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@Valid @RequestBody Transaction transaction) {
        return new ResponseEntity<>(transactionService.createTransaction(transaction), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return new ResponseEntity<>(transactionService.getAllTransactions(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return new ResponseEntity<>(transactionService.getTransactionById(id), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionByUserId(@PathVariable Long userId) {
        return new ResponseEntity<>(transactionService.getTransactionsByUserId(userId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionById(@PathVariable Long id) {
        transactionService.deleteTransactionById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<Transaction> acceptTransaction(@PathVariable Long id) {
        return new ResponseEntity<>(transactionService.updateTransactionStatus(id, true), HttpStatus.OK);
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<Transaction> rejectTransaction(@PathVariable Long id) {
        return new ResponseEntity<>(transactionService.updateTransactionStatus(id, false), HttpStatus.OK);
    }

    @PatchMapping("/{id}/swapoffer")
    public ResponseEntity<Transaction> updateSwapOffer(@PathVariable Long id, @RequestParam String swapOffer) {
        return new ResponseEntity<>(transactionService.updateSwapOffer(id, swapOffer), HttpStatus.OK);
    }

}
