# plantSwap

**This is a REST API using Spring boot and postgres. The application the backend part of a plantSwap service with which users can publish plant listings and acquire plant listed by other users.**

**The application is called plantSwap, but does actually allow for both selling and swapping plants (let's pretend that the platform started as a place for users to swap plants, and then added a functionality for selling plants but kept the name plantSwap)**

---
## Table of Content
1. Getting started
   1. Prerequisites
   2. Installation
   3. Usage
2. Functionality
   1. General description
   2. Detailed application rules 
   3. Limitations 
   4. Possible improvements 
3. Additional info
   1. Retired Transactions PUT methods

---

## Getting started
### Prerequisites
- postgres
- Docker
- Postman

### Installation
1. clone the repository and open your IDE of choice
    ```
    git clone https://github.com/tlind-gh/BackendContext_assignment1_postgres.git
    ```
2. add a .env file (in the source folder) and copy-paste the code below into it (change relevant fields to the username and password for your postgres user)
    #### */.env*
    
    ```
    POSTGRES_DB: plantSwap
    POSTGRES_USER: {your postgres username}
    POSTGRES_PASSWORD: {your postgres password}
    ```
    
3. add a application.yml file (in /src/main/resources) and copy-paste the code below into it (change relevant fields to the username and password for your postgres user) 

    #### */src/main/resources/application.yml*
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
4. create and empty postgres database called "plantSwap" (e.g., using pgAdmin)

### Usage
1. start a detached instance of docker by running the "docker-compose up -d" command in the folder of the cloned repository
2. run the PlantSwapPostgresApplication from your IDE
3. test the application using Postman (/src/main/ contains json files with data that can be used for testing)

---

## Functionality

### General description
The application is a REST API using Spring boot and postgres. The application lacks a frontend implementation and is best tested using Postman or equivalent.

The application has three main model classes: 1) User, 2) Plant and 3) Transaction. An instance of the User class represent a registered user on the plantSwap service. An instance of the Plant class represents a plant listing posted by a user. An instance of the Transaction class represents an offer on an plant listing by a user on the platform. 

The application allows a user to publish plant listings with details about the plant, and a price (if the plant is for sale) or a swap conditions (specifying what kind of plant the user wants in exchange for the listed plant). A plant listing must either be for sale (and have a price) or for swap (and have swap conditions), but never both.

Other users on the platform can buy plants which published as being for sale, and make swap offers on plant listings that are for swap.
Transactions which are of the purchase type are immediately accepted, as the price on the listing has been met by the buyer and both parties are thereby considered to be in agreement.
Transaction that which contain swap offers must be accepted by the owner of the plant listing before being accepted, as the swap offer might not be agreeable to the plant owner and they may wish to reject the proposed swap. 

### Detailed rules by Class

**Users**
- A user must have a unique username and a unique email address
- A user can have maximum 10 plant listings with status available at the same time (no limit on number of not available plants for one in user in the database)
- A user cannot be deleted if it owns plants with pending transaction or made a transaction offer that has status pending or accepted
  - If the plant has a pending transaction, the user must first accept/reject this transaction, and if the user has a pending transaction they should first delete this transaction
  - The reason for not allowing users with accepted transaction to be deleted is that the delete option in the database is set to cascade, so this would delete the accepted transactions leaving the plants with status not available but without an accepted transaction, this is something I have been trying to avoid in the rest of the program, to preserve either both the plant and the transaction or delete both (deleting the plant). Therefor users with accepted transactions cannot be deleted, even though this is also a bit annoying/suboptimal
- Deleting a user deletes the transactions of the user, plants owned by that user and the transactions linked to those plants

**Plants**
- A plant listing can only have 1 owner, but one user can own many plants
- A plant listing must have a common name, and scientific name (both family and species), since this is a serious plant swapping app where such info is essential
- A plant listing must have a specified care difficulty between 1 and 5 (5 being very difficult, i.e., requires a lot of special care)
- If plant size, stage, light requirement and water requirement are not specified when adding a new plant, they are auto-set to "unspecified"
- A plant must be either for sale (and then have a price specified) OR for swap (and then have swap conditions) - exactly one of these fields must be null.
- A plant with a pending transaction cannot be deleted (the transaction must first be rejected/accepted/deleted)
- Deleting a plant deletes all transactions linked to that plant

**Transactions**
- A transaction is linked to one buyer and one plant
- A transaction can not be made for a plant that does not have the status available
- The buyer must NOT be the same user as owns the plant (i.e., purchasing ones own plant is not allowed)
- A transaction linked to a plant with a swap condition must have a swap offer
- A transaction linked to a plant with a price must NOT have a swap offer
- Transactions for plants with a price are set to accepted automatically (and the plant is set to not available), since the price has been met
- Transaction for plants for swap are set as pending (and plant is set to reserved), as the swap offer must be accepted (or rejected) by the owner of the plant as well
- Swap offers of pending transactions can be edited, should the "buyer" want to update/change their offer
- Accepted transaction cannot be deleted (this is to preserve the data of the transaction for as long as the plant is still in the database, and not have not_available plants w/o transactions)

**All models**
- All entities have timestamps for when they were created and last updated (updated initially set to null)

### Limitations
- Common name, plant family and plant species can be filled in freely. It would be better if it was restricted to plants that actually exists and that choosing a plant family also narrowed down the options for the plant species to actual specie within that family (and also that the app checked correlation between common name and scientific name)
- The LocalTimeDate format used for the created_at and updated_at fields does not include a timezone
- Passwords are not encrypted (i.e., are saved in raw format in the database)


### Possible improvements
- Implementing hash and salting of the user password data
-  Limiting the common name and scientific names for the plants to actual plants (by importing a data from a database of plant names?)
- Move the getPlantsbyUserId to the plantService class, since the method is for getting data from the plants table and should logically be in th PlantService class (alternativley move the getTransactionsByUserID to the UserService class, to be consistent)
- Allow for deleting a user with accepted transactions and set user field to null for these transactions
  - This would mean changing the cascade setting for deleting and also allowing null for the buyer_id field in the transaction table

---

## Additional info

### Retired Transaction PUT methods
The follow Transaction methods (in TransactionService and TransactionController) was removed after it became superfluous
due to the addition of three patch-methods (for updating the SwapOffer, or rejecting/accepting a Transaction). These patch methods
cover all utilities of the update put-method, since the update method did not allow for updating the plant or user id.

#### *TransactionService class method*
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

#### *TransactionController class method*
```
@PutMapping("/{id}")
public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @Valid @RequestBody Transaction transaction) {
   return new ResponseEntity<>(transactionService.updateTransaction(id, transaction), HttpStatus.OK);
}
```