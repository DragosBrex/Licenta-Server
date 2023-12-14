package org.licenta.projectSAP.sapRepository.entity;

import lombok.Data;

import java.util.List;


@Data
public class TrainingTestingResults {
    private List<Double> actualValues;
    private List<Double> predictedValues;
}
