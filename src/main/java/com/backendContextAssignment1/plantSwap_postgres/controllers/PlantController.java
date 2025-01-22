package com.backendContextAssignment1.plantSwap_postgres.controllers;

import com.backendContextAssignment1.plantSwap_postgres.Services.PlantService;
import com.backendContextAssignment1.plantSwap_postgres.models.Plant;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    }

    @GetMapping
    public ResponseEntity<List<Plant>> getAllPlants() {
        return ResponseEntity.ok(plantService.getAllPlants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plant> getPlantById(@PathVariable Long id) {
        return ResponseEntity.ok(plantService.getPlantById(id));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Plant>> getAvailablePlants() {
        return ResponseEntity.ok(plantService.getAvailablePlants());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlantById(@PathVariable Long id) {
        plantService.deletePlantById(id);
        return ResponseEntity.noContent().build();
    }
}
