package com.backendContextAssignment1.plantSwap_postgres.models;

import com.backendContextAssignment1.plantSwap_postgres.models.supportClasses.TransactionStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity @Table(name = "transactions")
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //one plant can have more than one transaction if prev. transaction is rejected
    @ManyToOne(fetch = FetchType.EAGER)
    //not nullable
    @JoinColumn(name = "plant_id", nullable = false)
    @NotNull(message = "plant_id cannot be null")
    private Plant plant;

    //one user can have more than one transaction
    @ManyToOne(fetch = FetchType.EAGER)
    //not nullable
    @JoinColumn(name = "buyer_id", nullable = false)
    @NotNull(message = "buyer_id cannot be null")
    private User buyer;

    @Column(name = "status", nullable = false)
    @NotNull(message = "status cannot be null")
    private TransactionStatusEnum status;

    @Column(name = "swap_offer", length = 1000)
    @Size(max = 1000, message = "swap_offer cannot be longer than 1000 characters")
    private String swapOffer;

    @Column(name = "created_at", nullable = false, updatable = false)
    @NotNull(message = "created_at cannot be null")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    public Transaction() {
        createdAt = LocalDate.now();
    }

    public long getId() {
        return id;
    }

    public @NotNull(message = "plant_id cannot be null") Plant getPlant() {
        return plant;
    }

    public void setPlant(@NotNull(message = "plant_id cannot be null") Plant plant) {
        this.plant = plant;
    }

    public @NotNull(message = "plant_id cannot be null") User getBuyer() {
        return buyer;
    }

    public void setBuyer(@NotNull(message = "plant_id cannot be null") User buyer) {
        this.buyer = buyer;
    }

    public @NotNull(message = "status cannot be null") TransactionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(@NotNull(message = "status cannot be null") TransactionStatusEnum status) {
        this.status = status;
    }

    public @Size(max = 1000, message = "swap_offer cannot be longer than 1000 characters") String getSwapOffer() {
        return swapOffer;
    }

    public void setSwapOffer(@Size(max = 1000, message = "swap_offer cannot be longer than 1000 characters") String swapOffer) {
        this.swapOffer = swapOffer;
    }

    public @NotNull(message = "created_at cannot be null") LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}
