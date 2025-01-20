package com.backendContextAssignment1.plantSwap_postgres.controllers;

import com.backendContextAssignment1.plantSwap_postgres.Services.UserService;
import com.backendContextAssignment1.plantSwap_postgres.models.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //add @Valid
    //add error handling if all required fields are in not in the request body
    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));

        /* comment: EXAMPLE from bookShop w. mongodb -> change code to fit here.
        if(user.getAuthor() != null) {
            Author author = authorRepository.findById(book.getAuthor().getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Author not found"));
            book.setAuthor(author);
        }
    */
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
