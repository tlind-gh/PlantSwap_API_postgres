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
        private LocalDate created_at;
        private LocalDate updated_at;

        public User() {
            created_at = LocalDate.now();
            updated_at = null;
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

    public LocalDate getCreated_at() {
        return created_at;
    }


    public LocalDate getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDate updated_at) {
        this.updated_at = updated_at;
    }
}

