package com.backendContextAssignment1.plantSwap_postgres.controllers;

import com.backendContextAssignment1.plantSwap_postgres.Services.PlantService;
import com.backendContextAssignment1.plantSwap_postgres.models.Plant;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
public class PlantController {
    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @PostMapping
    public ResponseEntity<Plant> addPlant(@Valid @RequestBody Plant plant) {
        return ResponseEntity.status(HttpStatus.CREATED).body(plantService.createPlant(plant));

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
    public ResponseEntity<List<Plant>> getAllPlants() {
        return ResponseEntity.ok(plantService.getAllPlants());
    }

}
