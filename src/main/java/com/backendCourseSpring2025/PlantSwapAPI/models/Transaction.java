package com.backendCourseSpring2025.PlantSwapAPI.models;

import com.backendCourseSpring2025.PlantSwapAPI.models.supportClasses.TransactionStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

/*Instances of the Transaction class holds information about a proposed or performed transaction.
A Transaction is posted by a user (stored in Buyer variable) for an existing plant listing (stored in Plant variable).
If the plant is for sale, the Transaction has status ACCEPTED immediately, and if the plant is for swap the transaction has
status SWAP_PENDING (awaiting acceptance or rejection from the owner of the plant)*/

@Entity @Table(name = "transactions")
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "plant_id", nullable = false, updatable = false)
    @NotNull(message = "plant id cannot be null")
    private Plant plant;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "buyer_id", nullable = false, updatable = false)
    @NotNull(message = "buyer id cannot be null")
    private User buyer;

    @Column(name = "status", nullable = false)
    private TransactionStatusEnum status;

    @Column(name = "swap_offer", length = 1000)
    @Size(max = 1000, message = "swapOffer cannot be longer than 1000 characters")
    private String swapOffer;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Transaction() {
    }

    public long getId() {
        return id;
    }

    public @NotNull(message = "plant id cannot be null") Plant getPlant() {
        return plant;
    }

    public void setPlant(@NotNull(message = "plant id cannot be null") Plant plant) {
        this.plant = plant;
    }

    public @NotNull(message = "buyer id cannot be null") User getBuyer() {
        return buyer;
    }

    public void setBuyer(@NotNull(message = "buyer id cannot be null") User buyer) {
        this.buyer = buyer;
    }

    public TransactionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(TransactionStatusEnum status) {
        this.status = status;
    }

    public @Size(max = 1000, message = "swapOffer cannot be longer than 1000 characters") String getSwapOffer() {
        return swapOffer;
    }

    public void setSwapOffer(@Size(max = 1000, message = "swapOffer cannot be longer than 1000 characters") String swapOffer) {
        this.swapOffer = swapOffer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
