package org.licenta.projectSAP.sapRepository.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.List;


@Data
@Embeddable
public class TrainingTestingResults {
    private List<Double> actualValues;
    private List<Double> predictedValues;
}
