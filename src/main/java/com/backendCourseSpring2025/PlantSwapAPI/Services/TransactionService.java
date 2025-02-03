package com.backendCourseSpring2025.PlantSwapAPI.Services;

import com.backendCourseSpring2025.PlantSwapAPI.models.Plant;
import com.backendCourseSpring2025.PlantSwapAPI.models.Transaction;
import com.backendCourseSpring2025.PlantSwapAPI.models.supportClasses.TransactionStatusEnum;
import com.backendCourseSpring2025.PlantSwapAPI.repositories.PlantRepository;
import com.backendCourseSpring2025.PlantSwapAPI.repositories.TransactionRepository;
import com.backendCourseSpring2025.PlantSwapAPI.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

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

    //create and add new transaction
    public Transaction createTransaction(Transaction transaction) {
        //checks that user is not null for a new transaction
        if (transaction.getBuyer() == null) {
            throw new IllegalArgumentException("buyer id cannot be null for a new transaction");
        }

        //check that user exists
        if (!userRepository.existsById(transaction.getBuyer().getId())) {
            throw new NoSuchElementException("buyer id does not correspond to any existing user");
        }
        //check that plant exists and save plant for further checks
        Plant plant = plantRepository.findById(transaction.getPlant().getId())
                .orElseThrow(() -> new NoSuchElementException("plant id does not correspond to any existing plant"));

        //check that the plant for the transaction is available
        if (!transactionRepository.findByPlantAndStatus(plant, TransactionStatusEnum.ACCEPTED).isEmpty()
                && !transactionRepository.findByPlantAndStatus(plant, TransactionStatusEnum.SWAP_PENDING).isEmpty()) {
            throw new IllegalArgumentException("plant is reserved or not available");
        }

        //check that the user making the offer is not the owner of the plant
        if (Objects.equals(transaction.getBuyer().getId(), plant.getUser().getId())) {
           throw new IllegalArgumentException("buyer id cannot be the same as user id for the owner of the plant for the transaction");
        }

        //check that transaction has swapOffer if the plant has swapConditions (or does not have swapOffer, if the plant is for sale and not swap)
        if (plant.getPrice() == null && transaction.getSwapOffer() == null || plant.getPrice() != null && transaction.getSwapOffer() != null) {
            throw new IllegalArgumentException("transaction for plants with swap conditions must have a swap offer, transaction for plants with a price must NOT have a swap offer");
        }

        //set status for transaction and update status for plant (SWAP_PENDING/RESERVED or ACCEPTED/NOT_AVAILABLE, depending on if it is for swap or for sale)
        TransactionStatusEnum status = (plant.getPrice() == null) ? TransactionStatusEnum.SWAP_PENDING : TransactionStatusEnum.ACCEPTED;
        transaction.setStatus(status);

        //sets createdAt and updatedAt timestamps, overriding any potential user input from RequestBody for these variables
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(null);

        return transactionRepository.save(transaction);
    }

    //method for both rejecting and accepting pending transactions, depending on boolean input argument
    public Transaction updateTransactionStatus(Long id, boolean isAccepted) {
        Transaction transaction = validateTransactionIdAndReturnTransaction(id);
        //check that transaction is pending
        validateSwapPendingStatus(transaction);

        //set statusEnum according to boolean input
        TransactionStatusEnum status = isAccepted ? TransactionStatusEnum.ACCEPTED : TransactionStatusEnum.SWAP_REJECTED;

        transaction.setStatus(status);
        transaction.setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    //method for updating a swap offer, can only be done on pending transactions
    public Transaction updateSwapOffer(Long id, String newSwapOffer) {
        Transaction transaction = validateTransactionIdAndReturnTransaction(id);
        validateSwapPendingStatus(transaction);
        transaction.setSwapOffer(newSwapOffer);
        transaction.setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    //get all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    //get single transaction using id
    public Transaction getTransactionById(Long id) {
        return validateTransactionIdAndReturnTransaction(id);
    }

    //get all transactions for a user using user id
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByBuyer(userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Id does not correspond to any existing user")));
    }

    //only pending and rejected transactions can be deleted (deletion of accepted transactions is made by deleting the plant)
    public void deleteTransactionById(Long id) {
        Transaction transaction = validateTransactionIdAndReturnTransaction(id);
        if (transaction.getStatus() == TransactionStatusEnum.ACCEPTED) {
            throw new UnsupportedOperationException("accepted transaction cannot be deleted directly (delete the relevant plant to remove both plant and transaction)");
        }
        transactionRepository.deleteById(id);
    }

    /*validates that transaction exists in database, either casts exception or returns a Transaction
    (findByID() returns Optional<Transaction>, but can be saved as Transaction if expression includes casting exceptions if no Transaction is returned by repository*/
    private Transaction validateTransactionIdAndReturnTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Id does not correspond to any existing transaction"));
    }

    /*validate that a transaction is pending, checks both "status" and "swapOffer"
    (a pending transaction should always have a swapOffer, so the dual check should be redundant if application works properly,
    but the dual check is done for validation robustness)*/
    private void validateSwapPendingStatus(Transaction transaction) {
        if (transaction.getStatus() != TransactionStatusEnum.SWAP_PENDING || transaction.getSwapOffer() == null) {
            throw new IllegalArgumentException("Can only accept/reject transaction or alter swapOffer on transactions with status 'swap_pending'");
        }
    }

}
