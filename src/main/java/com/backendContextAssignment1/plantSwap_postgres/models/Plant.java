package com.backendContextAssignment1.plantSwap_postgres.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "plants")
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDate created_at;
    private LocalDate updated_at;

    public Plant() {
        created_at = LocalDate.now();
        updated_at = null;
    }
}
