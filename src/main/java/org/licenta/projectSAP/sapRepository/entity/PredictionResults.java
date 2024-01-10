package org.licenta.projectSAP.sapRepository.entity;


import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.List;

@Data
@Embeddable
public class PredictionResults {
    private List<Double> predictionValues;
}
