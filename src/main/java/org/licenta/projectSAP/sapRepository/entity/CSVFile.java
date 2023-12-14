package org.licenta.projectSAP.sapRepository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@Entity
public class CSVFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String path;
    private double size;

    @JsonIgnore
    @OneToOne(mappedBy = "trainingAndTestingDataFile")
    private MachineLearningModel trainingAndTestingModel;

    @JsonIgnore
    @OneToOne(mappedBy = "predictingDataFile")
    private MachineLearningModel predictingModel;
}
