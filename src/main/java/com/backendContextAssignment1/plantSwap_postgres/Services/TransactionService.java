package com.backendContextAssignment1.plantSwap_postgres.Services;

import com.backendContextAssignment1.plantSwap_postgres.models.Plant;
import com.backendContextAssignment1.plantSwap_postgres.models.Transaction;
import com.backendContextAssignment1.plantSwap_postgres.models.supportClasses.PlantAvailabilityStatusEnum;
import com.backendContextAssignment1.plantSwap_postgres.models.supportClasses.TransactionStatusEnum;
import com.backendContextAssignment1.plantSwap_postgres.repositories.PlantRepository;
import com.backendContextAssignment1.plantSwap_postgres.repositories.TransactionRepository;
import com.backendContextAssignment1.plantSwap_postgres.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

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

    public Transaction createTransaction(Transaction transaction) {
        if (!userRepository.existsById(transaction.getBuyer().getId())) {
            throw new NoSuchElementException("buyer id does not correspond to any existing user");
        }
        if (!plantRepository.existsById(transaction.getPlant().getId())) {
            throw new NoSuchElementException("plant id does not correspond to any existing user");
        }

        //get plant for the transaction to check that the new transaction corresponds to data in plant
        Plant plant = plantRepository.getReferenceById(transaction.getPlant().getId());
        if (plant.getAvailabilityStatus() != PlantAvailabilityStatusEnum.AVAILABLE) {
            throw new IllegalArgumentException("plant is reserved or not available");
        }
        if (transaction.getBuyer().getId() == plant.getUser().getId()) {
           throw new IllegalArgumentException("buyer id cannot be the same as user id for the owner of the plant for the transaction");
        }
        if (plant.getPrice() == null && transaction.getSwapOffer() == null || plant.getPrice() != null && transaction.getSwapOffer() != null) {
            throw new IllegalArgumentException("transaction for plants with swap conditions must have a swap offer, transaction for plants with a price must NOT have a swap offer");
        }

        //update status for transaction and plant depending on if it is for swap or for sale
        TransactionStatusEnum status = (plant.getPrice() == null) ? TransactionStatusEnum.SWAP_PENDING : TransactionStatusEnum.ACCEPTED;

        updateTransactionAndPlantStatus(transaction, status);

        //sets createdAt and updatedAt timestamps, overriding any potential user input from RequestBody for these variables
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(null);

        return transactionRepository.save(transaction);
    }

    //method for both rejecting and accepting pending transactions
    public Transaction updateTransactionStatus(Long id, boolean isAccepted) {
        Transaction transaction = validateTransactionIdAndReturnTransaction(id);
        validateSwapPendingStatus(transaction);

        //set statusEnum according to boolean input
        TransactionStatusEnum status = isAccepted ? TransactionStatusEnum.ACCEPTED : TransactionStatusEnum.SWAP_REJECTED;

        //update transaction and plant status accordingly
        updateTransactionAndPlantStatus(transaction, status);
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

    //only because it is a part of the Assignment description, is superfluous since there are patch methods covering all updatable fields
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

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return validateTransactionIdAndReturnTransaction(id);
    }

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
        //when deleting a pending transaction, the status of the plant is updated to available (same as if the swap is rejected)
        if (transaction.getStatus() == TransactionStatusEnum.SWAP_PENDING) {
            updateTransactionAndPlantStatus(transaction, TransactionStatusEnum.SWAP_REJECTED);
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

    //method for updating transaction and plant status.
    private void updateTransactionAndPlantStatus(Transaction transaction, TransactionStatusEnum transactionStatus) {
        transaction.setStatus(transactionStatus);
        Plant plant = plantRepository.getReferenceById(transaction.getPlant().getId());
        switch (transactionStatus) {
            case ACCEPTED -> plant.setAvailabilityStatus(PlantAvailabilityStatusEnum.NOT_AVAILABLE);
            case SWAP_PENDING -> plant.setAvailabilityStatus(PlantAvailabilityStatusEnum.RESERVED);
            case SWAP_REJECTED -> plant.setAvailabilityStatus(PlantAvailabilityStatusEnum.AVAILABLE);
        }
        plant.setUpdatedAt(LocalDateTime.now());
        plantRepository.save(plant);
    }

}
