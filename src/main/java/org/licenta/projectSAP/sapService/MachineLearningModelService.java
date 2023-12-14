package org.licenta.projectSAP.sapService;

import org.licenta.projectSAP.sapRepository.entity.MachineLearningModel;
import org.licenta.projectSAP.sapRepository.entity.PredictionResults;
import org.licenta.projectSAP.sapRepository.entity.TrainingTestingResults;

import java.util.List;

public interface MachineLearningModelService {
    MachineLearningModel createMachineLearningModel(MachineLearningModel model);

    MachineLearningModel getMachineLearningModelById(Long id);

    List<MachineLearningModel> getMachineLearningModelsByUser(String username);

    List<MachineLearningModel> getAllMachineLearningModels();

    void deleteMachineLearningModelById(Long id);

    void deleteMachineLearningModelByName(String modelName);

    TrainingTestingResults trainAndTestMachineLearningModel(MachineLearningModel model);

    PredictionResults predictUsingAModel(MachineLearningModel model);

    void TestStuff();
}
