package org.licenta.projectSAP.sapRepository.entity;


import lombok.Data;

import java.util.List;

@Data
public class PredictionResults {
    private List<Double> predictedValues;
}
