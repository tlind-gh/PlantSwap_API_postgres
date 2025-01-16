package com.backendContextAssignment1.plantSwap_postgres.models;

import com.backendContextAssignment1.plantSwap_postgres.models.supportClasses.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "plants")
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    //make most not null
    private String commonName;
    private String plantFamily;
    private String plantGenus;
    private PlantSizeEnum plantSize;
    private PlantStageEnum plantStage;
    //max min
    private int careDifficulty;
    private PlantLightRequirementEnum lightRequirement;
    private PlantWaterRequirementEnum waterRequirement;
    private String imageURL;
    @Column(length = 1000)
    private String description;
    private BigDecimal price;
    private String swapConditions;
    private PlantAvailabilityStatusEnum availabilityStatus;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public Plant() {
        //should this be here or in Controller class???
        if ((price == null && swapConditions == null) || (price != null && swapConditions != null)){
            throw new IllegalArgumentException();
        }
        createdAt = LocalDate.now();
        updatedAt = null;
    }
}
