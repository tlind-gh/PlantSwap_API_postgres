package com.backendContextAssignment1.plantSwap_postgres.models;

import com.backendContextAssignment1.plantSwap_postgres.models.supportClasses.TransactionStatusEnum;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    //one plant can have more than one transaction if prev. transaction is rejected
    @ManyToOne(fetch = FetchType.EAGER)
    //not nullable
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant_id;
    //one user can have more than one transaction
    @ManyToOne(fetch = FetchType.EAGER)
    //not nullable
    @JoinColumn(name = "user_buyer_id", nullable = false)
    private User buyer_user_id;
    //notnull
    private TransactionStatusEnum status;
    @Column(length = 1000)
    private String swap_offer;
    //notnull
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public Transaction() {
        //set IF plant_id Plant has price, status= ACCEPTED, swapOffer must be null,
        //IF price = null, swapOffer must not be null and status = swap_pending.
        createdAt = LocalDate.now();
        updatedAt = null;
    }

}
