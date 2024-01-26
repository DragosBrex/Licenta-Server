package org.licenta.projectSAP.sapRepository.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class CorrelationRequest {
    private String filePath;
    private String signalsToPredict;
}
