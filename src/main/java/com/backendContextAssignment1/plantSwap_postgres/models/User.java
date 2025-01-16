package com.backendContextAssignment1.plantSwap_postgres.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;
        //notnull and unique maybe min length
        private String username;
        //notnull, match pattern, hash and salt
        private String password;
        private LocalDate createdAt;
        private LocalDate updatedAt;

        public User() {
            createdAt = LocalDate.now();
            updatedAt = null;
        }

    public long getId() {
        return id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdated_at(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}

