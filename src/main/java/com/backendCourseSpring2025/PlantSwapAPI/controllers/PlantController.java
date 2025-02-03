package com.backendCourseSpring2025.PlantSwapAPI.controllers;

import com.backendCourseSpring2025.PlantSwapAPI.Services.PlantService;
import com.backendCourseSpring2025.PlantSwapAPI.models.Plant;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//REST controller for the Plant class. Handles Plants indirectly via PlantService class.
@RestController
@RequestMapping("/api/plants")
public class PlantController {
    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @PostMapping
    public ResponseEntity<Plant> addPlant(@Valid @RequestBody Plant plant) {
        return new ResponseEntity<>(plantService.createPlant(plant), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Plant>> getAllPlants() {
        return new ResponseEntity<>(plantService.getAllPlants(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plant> getPlantById(@PathVariable Long id) {
        return new ResponseEntity<>(plantService.getPlantById(id), HttpStatus.OK);
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<List<Plant>> getPlantByUserId(@PathVariable Long userId) {
        return new ResponseEntity<>(plantService.getPlantByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Plant>> getAvailablePlants() {
        return new ResponseEntity<>(plantService.getAvailablePlants(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlantById(@PathVariable Long id) {
        plantService.deletePlantById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Plant> updatePlant(@PathVariable Long id, @Valid @RequestBody Plant plant) {
        return new ResponseEntity<>(plantService.updatePlant(id, plant), HttpStatus.OK);
    }
}
