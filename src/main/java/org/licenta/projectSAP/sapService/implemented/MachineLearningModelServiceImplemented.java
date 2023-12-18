package org.licenta.projectSAP.sapService.implemented;

import org.licenta.projectSAP.sapRepository.CSVFileRepository;
import org.licenta.projectSAP.sapRepository.MachineLearningModelRepository;
import org.licenta.projectSAP.sapRepository.UserRepository;
import org.licenta.projectSAP.sapRepository.entity.MachineLearningModel;
import org.licenta.projectSAP.sapRepository.entity.PredictionResults;
import org.licenta.projectSAP.sapRepository.entity.TrainingTestingResults;
import org.licenta.projectSAP.sapService.MachineLearningModelService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class MachineLearningModelServiceImplemented implements MachineLearningModelService {

    private final MachineLearningModelRepository machineLearningModelRepository;
    private final UserRepository userRepository;
    private final CSVFileRepository csvFileRepository;

    public MachineLearningModelServiceImplemented(MachineLearningModelRepository machineLearningModelRepository, UserRepository userRepository, CSVFileRepository csvFileRepository) {
        this.machineLearningModelRepository = machineLearningModelRepository;
        this.userRepository = userRepository;
        this.csvFileRepository = csvFileRepository;
    }

    @Override
    public CompletableFuture<MachineLearningModel> createMachineLearningModel(MachineLearningModel model) {
        return CompletableFuture.completedFuture(machineLearningModelRepository.save(model));
    }

    @Override
    public CompletableFuture<MachineLearningModel> getMachineLearningModelById(Long id) {
        return CompletableFuture.completedFuture(machineLearningModelRepository.findById(id).orElse(null));
    }

    @Override
    public CompletableFuture<List<MachineLearningModel>> getMachineLearningModelsByUser(String username) {
//        return machineLearningModelRepository.findByUser(username);
        return CompletableFuture.completedFuture(machineLearningModelRepository.findAll());
    }

    @Override
    public CompletableFuture<List<MachineLearningModel>> getAllMachineLearningModels() {
        return CompletableFuture.completedFuture(machineLearningModelRepository.findAll());
    }

    @Override
    public CompletableFuture<MachineLearningModel> deleteMachineLearningModelById(Long id) {
        MachineLearningModel machineLearningModel = machineLearningModelRepository.findById(id).get();
        machineLearningModelRepository.deleteById(id);

        return CompletableFuture.completedFuture(machineLearningModel);
    }

    @Override
    public CompletableFuture<MachineLearningModel> deleteMachineLearningModelByName(String modelName) {
        MachineLearningModel machineLearningModel = machineLearningModelRepository.findByName(modelName);
        machineLearningModelRepository.deleteMachineLearningModelByName(modelName);

        return CompletableFuture.completedFuture(machineLearningModel);
    }

    @Override
    public CompletableFuture<TrainingTestingResults> trainAndTestMachineLearningModel(MachineLearningModel model) {
        try {
            String powerShellScript = "python 'C:\\Users\\drago\\Desktop\\Licenta Brisc Dragos-Nicolae\\Algorithm\\SAPModel.py' '"
                    + model.getName()
                    + "' '" + model.getTrainingAndTestingDataFile().getPath()
                    + "' '" + model.getSelectedTimeInterval()
                    + "' '" + model.getTimeSpan()
                    + "' '" + model.isTimeDependency()
                    + "' '" + String.join(" ", model.getSignalsToPredict())
                    + "' '" + String.join(" ", model.getSignalsWithInfluence())
                    + "' " + model.getPastSteps()
                    + " " + model.getFutureSteps()
                    + " " + model.getTrainTestSplit()
                    + " " + model.getAlgorithm()
                    + " " + model.getEpochs();

            String command = "powershell.exe -ExecutionPolicy Bypass -NoProfile -Command " + powerShellScript;

            Process process = Runtime.getRuntime().exec(command);

            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            String line;
            while ((line = errorReader.readLine()) != null) {
                System.err.println("Error: " + line);
            }

            int exitCode = process.waitFor();
            System.out.println("Command exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        TrainingTestingResults trainingTestingResults = new TrainingTestingResults();

        String trainingResultsPath = "C:\\Users\\drago\\Desktop\\Licenta Brisc Dragos-Nicolae\\Machine Learning Models\\trainingAndTestingOutputs\\" + model.getName() + ".txt";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(trainingResultsPath));

            List<Double> actualValues = new ArrayList<>();
            List<Double> predictedValues = new ArrayList<>();
            boolean isSecondArray = false;

            String line;
            while ((line = reader.readLine()) != null) {
                if ("separator".equals(line.trim())) {
                    isSecondArray = true;
                    continue;
                }

                double value = Double.parseDouble(line.trim());

                if (isSecondArray) {
                    predictedValues.add(value);
                } else {
                    actualValues.add(value);
                }
            }

            trainingTestingResults.setActualValues(actualValues.subList(1,actualValues.size()));
            trainingTestingResults.setPredictedValues(predictedValues);

            reader.close();

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        model.setTrainingTestingResults(trainingTestingResults);

        csvFileRepository.save(model.getTrainingAndTestingDataFile());
        machineLearningModelRepository.save(model);

        System.out.println(trainingTestingResults);

        return CompletableFuture.completedFuture(trainingTestingResults);
    }

    @Override
    public CompletableFuture<PredictionResults> predictUsingAModel(MachineLearningModel model) {
        try {
            String powerShellScript = "python 'C:\\Users\\drago\\Desktop\\Licenta Brisc Dragos-Nicolae\\Algorithm\\SAPPredict.py' '"
                    + model.getName()
                    + "' '" + model.getPredictingDataFile().getPath()
                    + "' '" + model.getSelectedTimeInterval()
                    + "' '" +  model.getTimeSpan()
                    + "' '" + String.join(" ", model.getSignalsToPredict())
                    + "' '" + String.join(" ", model.getSignalsWithInfluence())
                    + "' " + model.getPastSteps();

            String command = "powershell.exe -ExecutionPolicy Bypass -NoProfile -Command " + powerShellScript;

            Process process = Runtime.getRuntime().exec(command);

            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            String line;
            while ((line = errorReader.readLine()) != null) {
                System.err.println("Error: " + line);
            }

            int exitCode = process.waitFor();
            System.out.println("Command exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        PredictionResults predictionResults = new PredictionResults();

        String predictionResultsPath = "C:\\Users\\drago\\Desktop\\Licenta Brisc Dragos-Nicolae\\Machine Learning Models\\predictingOutputs\\" + model.getName() + ".txt";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(predictionResultsPath));

            List<Double> predictedValues = new ArrayList<>();
            boolean isSecondArray = false;

            String line;
            while ((line = reader.readLine()) != null) {
                double value = Double.parseDouble(line.trim());
                predictedValues.add(value);
            }

            predictionResults.setPredictedValues(predictedValues);

            reader.close();

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        model.setPredictionResults(predictionResults);

        csvFileRepository.save(model.getPredictingDataFile());
        machineLearningModelRepository.findByName(model.getName()).setPredictingDataFile(model.getPredictingDataFile());

        System.out.println(predictionResults);

        return CompletableFuture.completedFuture(predictionResults);
    }

}
