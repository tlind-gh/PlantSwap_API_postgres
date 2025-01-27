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
- create empty db "plantSwap" in postgres
- go to repo directory and run "docker-compose up -d"

### src/main/resources/application.yml
```
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/plantSwap
    username: {your postgres username}
    password: {your postgres password}
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
    show-sql: true
```
### /.env 
```
POSTGRES_DB: plantSwap
POSTGRES_USER: {your postgres username}
POSTGRES_PASSWORD: {your postgres password}
```

## Retired Transaction update methods
The follow Transaction methods (in TransactionService and TransactionController) was removed after it became superfluous
due to the addition of three patch-methods (for updating the SwapOffer, or rejecting/accepting a Transaction). These patch methods
cover all utilities of the update put-method, since the update method did not allow for updating the plant or user id.

### TransactionService class method
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

### TransactionController class method 
```
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @Valid @RequestBody Transaction transaction) {
        return new ResponseEntity<>(transactionService.updateTransaction(id, transaction), HttpStatus.OK);
    }
```