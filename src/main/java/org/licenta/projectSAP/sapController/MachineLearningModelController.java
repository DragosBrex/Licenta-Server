package org.licenta.projectSAP.sapController;

import org.licenta.projectSAP.sapRepository.entity.MachineLearningModel;
import org.licenta.projectSAP.sapRepository.entity.PredictionResults;
import org.licenta.projectSAP.sapRepository.entity.TrainingTestingResults;
import org.licenta.projectSAP.sapService.MachineLearningModelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/models")
@CrossOrigin(origins = "*")
public class MachineLearningModelController {

    private final MachineLearningModelService mlService;

    public MachineLearningModelController(MachineLearningModelService mlService) {
        this.mlService = mlService;
    }

    @PostMapping("/create")
    public ResponseEntity<MachineLearningModel> createMachineLearningModel(@RequestBody MachineLearningModel model) {
        MachineLearningModel createdModel = mlService.createMachineLearningModel(model);
        return new ResponseEntity<>(createdModel, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MachineLearningModel> getMachineLearningModelById(@PathVariable Long id) {
        MachineLearningModel model = mlService.getMachineLearningModelById(id);
        if (model != null) {
            return new ResponseEntity<>(model, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<MachineLearningModel>> getMachineLearningModelsByUser(@PathVariable String username) {
        List<MachineLearningModel> models = mlService.getMachineLearningModelsByUser(username);
        return new ResponseEntity<>(models, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MachineLearningModel>> getAllMachineLearningModels() {
        List<MachineLearningModel> models = mlService.getAllMachineLearningModels();
        return new ResponseEntity<>(models, HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMachineLearningModelById(@PathVariable Long id) {
        mlService.deleteMachineLearningModelById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Transactional
    @DeleteMapping("/deleteName/{modelName}")
    public ResponseEntity<Void> deleteMachineLearningModelByName(@PathVariable String modelName) {
        mlService.deleteMachineLearningModelByName(modelName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/train")
    public ResponseEntity<TrainingTestingResults> trainAndTestMachineLearningModel(@RequestBody MachineLearningModel model) {
        TrainingTestingResults results = mlService.trainAndTestMachineLearningModel(model);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @PostMapping("/predict")
    public ResponseEntity<PredictionResults> predictUsingAModel(@RequestBody MachineLearningModel model) {
        PredictionResults results = mlService.predictUsingAModel(model);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}
