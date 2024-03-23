package org.licenta.projectSAP.sapService.implemented;

import jakarta.transaction.Transactional;
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
@Transactional
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
    public CompletableFuture<MachineLearningModel> getMachineLearningModelByName(String modelName) {
        return CompletableFuture.completedFuture(machineLearningModelRepository.findByName(modelName));
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
    @Transactional
    public CompletableFuture<MachineLearningModel> deleteMachineLearningModelByName(String modelName) {
        MachineLearningModel machineLearningModel = machineLearningModelRepository.findByName(modelName);
        machineLearningModelRepository.deleteMachineLearningModelByName(modelName);

        return CompletableFuture.completedFuture(machineLearningModel);
    }

    @Override
    public CompletableFuture<TrainingTestingResults> trainAndTestMachineLearningModel(MachineLearningModel model) {

        TrainingTestingResults trainingTestingResults = new TrainingTestingResults();

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

            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            List<Double> actualValues = new ArrayList<>();
            List<Double> predictedValues = new ArrayList<>();
            Double accuracy = 0.0;
            boolean passedStart = false;
            boolean passedEnd = false;
            boolean passedSeparator1 = false;
            boolean passedSeparator2 = false;

            while ((line = reader.readLine()) != null) {
                if("results start".equals(line.trim()) && !passedStart) {
                    passedStart = true;
                    continue;
                }

                if("separator1".equals(line.trim()) && !passedEnd && passedStart) {
                    passedSeparator1 = true;
                }

                if("separator2".equals(line.trim()) && !passedEnd && passedStart) {
                    passedSeparator2 = true;
                }

                if("results end".equals(line.trim()) && !passedEnd && passedStart) {
                    passedEnd = true;
                }

                if (passedStart && !passedEnd) {

                    if(line.matches("\\s*-?\\d+(\\.\\d+)?\\s*")) {
                        double value = Double.parseDouble(line);

                        if(!passedSeparator1) {
                            actualValues.add(value);
                        }

                        if (passedSeparator1 && !passedSeparator2) {
                            predictedValues.add(value);
                        }

                        if (passedSeparator2) {
                            accuracy = value;
                        }
                    }
                }

            }

            trainingTestingResults.setActualValues(actualValues);
            trainingTestingResults.setPredictedValues(predictedValues);
            trainingTestingResults.setAccuracy(accuracy);

            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            while ((line = errorReader.readLine()) != null) {
                System.err.println("Error: " + line);
            }

            int exitCode = process.waitFor();
            System.out.println("Command exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        model.setTrainingTestingResults(trainingTestingResults);
        model.setPredictionResults(new PredictionResults());

        csvFileRepository.save(model.getTrainingAndTestingDataFile());
        machineLearningModelRepository.save(model);

        return CompletableFuture.completedFuture(trainingTestingResults);
    }

    @Override
    public CompletableFuture<PredictionResults> predictUsingAModel(MachineLearningModel model) {

        PredictionResults predictionResults = new PredictionResults();

        try {
            String powerShellScript = "python 'C:\\Users\\drago\\Desktop\\Licenta Brisc Dragos-Nicolae\\Algorithm\\SAPPredict.py' '"
                    + model.getName()
                    + "' '" + model.getPredictingDataFile().getPath()
                    + "' '" + model.getSelectedTimeInterval()
                    + "' '" + model.getTimeSpan()
                    + "' '" + String.join(" ", model.getSignalsToPredict())
                    + "' '" + String.join(" ", model.getSignalsWithInfluence())
                    + "' '" + model.getPastSteps()
                    + "' '" + machineLearningModelRepository.findByName(model.getName()).getTrainingAndTestingDataFile().getPath() + "'";

            String command = "powershell.exe -ExecutionPolicy Bypass -NoProfile -Command " + powerShellScript;

            Process process = Runtime.getRuntime().exec(command);

            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            List<Double> predictedValues = new ArrayList<>();
            double pastCorrelation = 0.0;
            boolean passedStart = false;
            boolean passedEnd = false;
            boolean passedSeparator = false;

            while ((line = reader.readLine()) != null) {
                if("results start".equals(line.trim()) && !passedStart) {
                    passedStart = true;
                    continue;
                }

                if("results end".equals(line.trim()) && !passedEnd && passedStart) {
                    passedEnd = true;
                }

                if("separator".equals(line.trim()) && !passedEnd && passedStart) {
                    passedSeparator = true;
                }

                if (passedStart && !passedEnd) {
                    String[] parts = line.split("\\s+");

                    if (parts.length >= 2 && parts[1].matches("-?\\d+(\\.\\d+)?")) {
                        double value = Double.parseDouble(parts[1]);

                        if(!passedSeparator) {
                            predictedValues.add(value);
                        } else {
                            System.out.println("Asta e valoarea primita: " + value);
                            pastCorrelation = value;
                        }

                    }
                }
            }

            predictionResults.setPredictionValues(predictedValues);
            predictionResults.setPastCorrelation(pastCorrelation);

            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            while ((line = errorReader.readLine()) != null) {
                System.err.println("Error: " + line);
            }

            int exitCode = process.waitFor();
            System.out.println("Command exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        model.setPredictionResults(predictionResults);

        csvFileRepository.save(model.getPredictingDataFile());
        MachineLearningModel actualModel = machineLearningModelRepository.findByName(model.getName());
        actualModel.setPredictingDataFile(model.getPredictingDataFile());
        actualModel.setPredictionResults(model.getPredictionResults());
        machineLearningModelRepository.save(actualModel);

        return CompletableFuture.completedFuture(predictionResults);
    }

    @Override
    public CompletableFuture<List<String>> getCorrelationVector(String filePath, String signalsToPredict) {

        List<String> correlationVector = new ArrayList<>();

        try {
            String powerShellScript = "python 'C:\\Users\\drago\\Desktop\\Licenta Brisc Dragos-Nicolae\\Algorithm\\SAPCorrelation.py' '"
                    + "' '" + filePath
                    + "' '" + signalsToPredict + "'";

            String command = "powershell.exe -ExecutionPolicy Bypass -NoProfile -Command " + powerShellScript;

            Process process = Runtime.getRuntime().exec(command);

            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            boolean passedStart = false;
            boolean passedEnd = false;

            while ((line = reader.readLine()) != null) {
                if("correlation start".equals(line.trim()) && !passedStart) {
                    passedStart = true;
                    continue;
                }

                if("correlation end".equals(line.trim()) && !passedEnd && passedStart) {
                    passedEnd = true;
                }

                if (passedStart && !passedEnd) {
                    correlationVector.add(line);
                }

            }

            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            while ((line = errorReader.readLine()) != null) {
                System.err.println("Error: " + line);
            }

            int exitCode = process.waitFor();
            System.out.println("Command exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(correlationVector);
    }
}
