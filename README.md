# plantSwap

## General description
short description

## Functionality

### User
- points

### Plant
- points

### Transaction
- points
- removed update method, explain (at end of readme )

## Running the app
add things about .env, docker etc.

## Transaction update method

### TransactionService class
```
public Transaction updateTransaction(Long id, Transaction newTransaction) {
Transaction existingTransaction = validateTransactionIdAndReturnTransaction(id);
if (newTransaction.getPlant().getId() != existingTransaction.getPlant().getId() || newTransaction.getBuyer().getId() != existingTransaction.getBuyer().getId()) {
throw new IllegalArgumentException("plant id and buyer id cannot be changed");
}

        if (existingTransaction.getStatus() != TransactionStatusEnum.SWAP_PENDING && existingTransaction.getStatus() != newTransaction.getStatus()) {
            throw new IllegalArgumentException("status of accepted or rejected transactions cannot be changed");
        }

        if ((existingTransaction.getSwapOffer() == null && newTransaction.getSwapOffer() != null) || (existingTransaction.getSwapOffer() != null && newTransaction.getSwapOffer() == null)) {
            throw new IllegalArgumentException("swap offer cannot be added to transaction for a non-swappable plant and swap offer cannot be deleted from swappable plant");
        }

        //if status is updated from pending to accepted or rejected, then update the plant status
        if (existingTransaction.getStatus() == TransactionStatusEnum.SWAP_PENDING && newTransaction.getStatus() != TransactionStatusEnum.SWAP_PENDING) {
            updateTransactionAndPlantStatus(existingTransaction, newTransaction.getStatus());
        }

        existingTransaction.setSwapOffer(newTransaction.getSwapOffer());
        existingTransaction.setUpdatedAt(LocalDateTime.now());

        return transactionRepository.save(existingTransaction);
    }
```

### TransactionController class
```
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @Valid @RequestBody Transaction transaction) {
        return new ResponseEntity<>(transactionService.updateTransaction(id, transaction), HttpStatus.OK);
    }
```