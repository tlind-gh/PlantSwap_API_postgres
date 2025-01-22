package com.backendContextAssignment1.plantSwap_postgres.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username", unique = true, length = 50, nullable = false)
    @NotNull(message = "username cannot be null")
    @Size(min = 1, max = 50, message = "username must be 1 to 50 characters long")
    private String username;

    @Column(name = "password", length = 50, nullable = false)
    @NotNull(message = "password cannot be null")
    @Size(min = 1, max = 50, message = "password must between 8 and 50 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "password must contain minimum 1 uppercase letter, 1 lowercase letter and 1 digit")
    private String password;

    @Column(name = "email", unique = true, length = 200, nullable = false)
    @NotNull(message = "email cannot be null")
    @Size(min = 1, max = 200, message = "email must be between 1 and 50 characters long")
    @Email(message = "email address does not have a valid format")
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    @NotNull(message = "created_at cannot be null")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User() {
        createdAt = LocalDateTime.now();
    }

    //autogenerated getters and setters (w. validation annotations)
    //no setter for id and createdAt, since these should never be modified

    public long getId() {
        return id;
    }

    public @NotNull(message = "username cannot be null") @Size(min = 1, max = 50, message = "username must be 1 to 50 characters long") String getUsername() {
        return username;
    }

    public void setUsername(@NotNull(message = "username cannot be null") @Size(min = 1, max = 50, message = "username must be 1 to 50 characters long") String username) {
        this.username = username;
    }

    public @NotNull(message = "password cannot be null") @Size(min = 1, max = 50, message = "password must between 8 and 50 characters long") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "password must contain minimum 1 uppercase letter, 1 lowercase letter and 1 digit") String getPassword() {
        return password;
    }

    public void setPassword(@NotNull(message = "password cannot be null") @Size(min = 1, max = 50, message = "password must between 8 and 50 characters long") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "password must contain minimum 1 uppercase letter, 1 lowercase letter and 1 digit") String password) {
        this.password = password;
    }

    public @NotNull(message = "email cannot be null") @Size(min = 1, max = 200, message = "email must be between 1 and 50 characters long") @Email(message = "email address does not have a valid format") String getEmail() {
        return email;
    }

    public void setEmail(@NotNull(message = "email cannot be null") @Size(min = 1, max = 200, message = "email must be between 1 and 50 characters long") @Email(message = "email address does not have a valid format") String email) {
        this.email = email;
    }

    public @NotNull(message = "created_at cannot be null") LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

