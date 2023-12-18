package org.licenta.projectSAP.sapService;

import org.licenta.projectSAP.sapRepository.entity.MachineLearningModel;
import org.licenta.projectSAP.sapRepository.entity.PredictionResults;
import org.licenta.projectSAP.sapRepository.entity.TrainingTestingResults;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MachineLearningModelService {
    @Async
    CompletableFuture<MachineLearningModel> createMachineLearningModel(MachineLearningModel model);

    @Async
    CompletableFuture<MachineLearningModel> getMachineLearningModelById(Long id);

    @Async
    CompletableFuture<List<MachineLearningModel>> getMachineLearningModelsByUser(String username);

    @Async
    CompletableFuture<List<MachineLearningModel>> getAllMachineLearningModels();

    @Async
    CompletableFuture<MachineLearningModel> deleteMachineLearningModelById(Long id);

    @Async
    CompletableFuture<MachineLearningModel> deleteMachineLearningModelByName(String modelName);

    @Async
    CompletableFuture<TrainingTestingResults> trainAndTestMachineLearningModel(MachineLearningModel model);

    @Async
    CompletableFuture<PredictionResults> predictUsingAModel(MachineLearningModel model);
}
