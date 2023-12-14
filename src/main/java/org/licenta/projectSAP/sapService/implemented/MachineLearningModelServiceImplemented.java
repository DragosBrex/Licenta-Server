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

    public void TestStuff() {
        try {
            //String powerShellScript = "python 'C:\\Users\\drago\\Desktop\\Licenta Brisc Dragos-Nicolae\\Algorithm\\SAPModel.py' 'NICOLAEBOSS' 'C:\\Users\\drago\\Desktop\\Licenta Brisc Dragos-Nicolae\\Back-End\\FileStorage\\weather.csv' '20' '5' 'false' 'MaxTemp' 'MinTemp MaxTemp' 5 5 0.3 1 10";
            String powerShellScript = "python 'C:\\Users\\drago\\Desktop\\Licenta Brisc Dragos-Nicolae\\Algorithm\\SAPPredict.py' 'C:\\Users\\drago\\Desktop\\Licenta Brisc Dragos-Nicolae\\Back-End\\FileStorage\\weather.csv' '20' '5' 'MaxTemp' 'MinTemp MaxTemp' '5' 'NICOLAEBOSS'";

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
    }


    @Override
    public MachineLearningModel createMachineLearningModel(MachineLearningModel model) {
        return machineLearningModelRepository.save(model);
    }

    @Override
    public MachineLearningModel getMachineLearningModelById(Long id) {
        return machineLearningModelRepository.findById(id).orElse(null);
    }

    @Override
    public List<MachineLearningModel> getMachineLearningModelsByUser(String username) {
//        return machineLearningModelRepository.findByUser(username);
        return machineLearningModelRepository.findAll();
    }

    @Override
    public List<MachineLearningModel> getAllMachineLearningModels() {
        return machineLearningModelRepository.findAll();
    }

    @Override
    public void deleteMachineLearningModelById(Long id) {
        machineLearningModelRepository.deleteById(id);
    }

    @Override
    public void deleteMachineLearningModelByName(String modelName) {
        machineLearningModelRepository.deleteMachineLearningModelByName(modelName);
    }

    @Override
    public TrainingTestingResults trainAndTestMachineLearningModel(MachineLearningModel model) {
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

        return trainingTestingResults;
    }

    @Override
    public PredictionResults predictUsingAModel(MachineLearningModel model) {
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

        return predictionResults;
    }

}
