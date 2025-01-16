package com.backendContextAssignment1.plantSwap_postgres.controllers;

import com.backendContextAssignment1.plantSwap_postgres.models.Plant;
import com.backendContextAssignment1.plantSwap_postgres.repositories.PlantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
public class PlantController {
    private final PlantRepository plantRepository;

    public PlantController(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    @PostMapping
    public ResponseEntity<Plant> addPlant(@RequestBody Plant plant) {
        return ResponseEntity.status(HttpStatus.CREATED).body(plantRepository.save(plant));

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
        return ResponseEntity.ok(plantRepository.findAll());
    }

}
