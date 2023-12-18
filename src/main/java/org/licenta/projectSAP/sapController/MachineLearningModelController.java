package org.licenta.projectSAP.sapController;

import org.licenta.projectSAP.sapRepository.entity.MachineLearningModel;
import org.licenta.projectSAP.sapRepository.entity.PredictionResults;
import org.licenta.projectSAP.sapRepository.entity.TrainingTestingResults;
import org.licenta.projectSAP.sapService.MachineLearningModelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

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
    public DeferredResult<ResponseEntity<MachineLearningModel>> createMachineLearningModel(@RequestBody MachineLearningModel model) {
        DeferredResult<ResponseEntity<MachineLearningModel>> deferredResult = new DeferredResult<>();

        mlService.createMachineLearningModel(model)
                .whenComplete((createdModel, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(throwable);
                    } else {
                        deferredResult.setResult(new ResponseEntity<>(createdModel, HttpStatus.CREATED));
                    }
                });

        return deferredResult;
    }

    @GetMapping("/{id}")
    public DeferredResult<ResponseEntity<MachineLearningModel>> getMachineLearningModelById(@PathVariable Long id) {
        DeferredResult<ResponseEntity<MachineLearningModel>> deferredResult = new DeferredResult<>();

        mlService.getMachineLearningModelById(id)
                .whenComplete((model, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(throwable);
                    } else if (model != null) {
                        deferredResult.setResult(new ResponseEntity<>(model, HttpStatus.OK));
                    } else {
                        deferredResult.setResult(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                    }
                });

        return deferredResult;
    }

    @GetMapping("/user/{username}")
    public DeferredResult<ResponseEntity<List<MachineLearningModel>>> getMachineLearningModelsByUser(@PathVariable String username) {
        DeferredResult<ResponseEntity<List<MachineLearningModel>>> deferredResult = new DeferredResult<>();

        mlService.getMachineLearningModelsByUser(username)
                .whenComplete((models, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(throwable);
                    } else {
                        deferredResult.setResult(new ResponseEntity<>(models, HttpStatus.OK));
                    }
                });

        return deferredResult;
    }

    @GetMapping("/all")
    public DeferredResult<ResponseEntity<List<MachineLearningModel>>> getAllMachineLearningModels() {
        DeferredResult<ResponseEntity<List<MachineLearningModel>>> deferredResult = new DeferredResult<>();

        mlService.getAllMachineLearningModels()
                .whenComplete((models, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(throwable);
                    } else {
                        deferredResult.setResult(new ResponseEntity<>(models, HttpStatus.OK));
                    }
                });

        return deferredResult;
    }

    @Transactional
    @DeleteMapping("/{id}")
    public DeferredResult<ResponseEntity<Void>> deleteMachineLearningModelById(@PathVariable Long id) {
        DeferredResult<ResponseEntity<Void>> deferredResult = new DeferredResult<>();

        mlService.deleteMachineLearningModelById(id)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(throwable);
                    } else {
                        deferredResult.setResult(new ResponseEntity<>(HttpStatus.NO_CONTENT));
                    }
                });

        return deferredResult;
    }

    @Transactional
    @DeleteMapping("/deleteName/{modelName}")
    public DeferredResult<ResponseEntity<Void>> deleteMachineLearningModelByName(@PathVariable String modelName) {
        DeferredResult<ResponseEntity<Void>> deferredResult = new DeferredResult<>();

        mlService.deleteMachineLearningModelByName(modelName)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(throwable);
                    } else {
                        deferredResult.setResult(new ResponseEntity<>(HttpStatus.NO_CONTENT));
                    }
                });

        return deferredResult;
    }

    @PostMapping("/train")
    public DeferredResult<ResponseEntity<TrainingTestingResults>> trainAndTestMachineLearningModel(@RequestBody MachineLearningModel model) {
        DeferredResult<ResponseEntity<TrainingTestingResults>> deferredResult = new DeferredResult<>();

        mlService.trainAndTestMachineLearningModel(model)
                .whenComplete((results, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(throwable);
                    } else {
                        deferredResult.setResult(new ResponseEntity<>(results, HttpStatus.OK));
                    }
                });

        return deferredResult;
    }

    @PostMapping("/predict")
    public DeferredResult<ResponseEntity<PredictionResults>> predictUsingAModel(@RequestBody MachineLearningModel model) {
        DeferredResult<ResponseEntity<PredictionResults>> deferredResult = new DeferredResult<>();

        mlService.predictUsingAModel(model)
                .whenComplete((results, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(throwable);
                    } else {
                        deferredResult.setResult(new ResponseEntity<>(results, HttpStatus.OK));
                    }
                });

        return deferredResult;
    }
}
