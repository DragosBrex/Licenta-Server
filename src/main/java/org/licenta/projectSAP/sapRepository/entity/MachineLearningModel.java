package org.licenta.projectSAP.sapRepository.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@Data
@Entity
public class MachineLearningModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String name;

    @OneToOne
    @JoinColumn(name = "training_and_testing_data_file_id")
    private CSVFile trainingAndTestingDataFile;

    @OneToOne
    @JoinColumn(name = "predicting_data_file_id")
    private CSVFile predictingDataFile;

    private List<String> selectedTimeInterval = new ArrayList<>();

    private int timeSpan;
    private boolean timeDependency;

    private List<String> signalsToPredict = new ArrayList<>();

    private List<String> signalsWithInfluence = new ArrayList<>();

    private int pastSteps;
    private int futureSteps;

    private Float trainTestSplit;

    @Enumerated(EnumType.STRING)
    private MLAlgorithms algorithm;

    private Long epochs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private TrainingTestingResults trainingTestingResults;

    @Embedded
    private PredictionResults predictionResults;
}
