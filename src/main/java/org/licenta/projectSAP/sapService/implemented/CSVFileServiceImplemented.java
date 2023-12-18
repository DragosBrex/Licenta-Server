package org.licenta.projectSAP.sapService.implemented;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.commons.validator.routines.IntegerValidator;
import org.licenta.projectSAP.sapRepository.CSVFileRepository;
import org.licenta.projectSAP.sapRepository.entity.CSVFile;
import org.licenta.projectSAP.sapService.CSVFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class CSVFileServiceImplemented implements CSVFileService {
    private final CSVFileRepository csvFileRepository;

    private final String defaultPath = "C:\\Users\\drago\\Desktop\\Licenta Brisc Dragos-Nicolae\\Back-End\\FileStorage";

    public CSVFileServiceImplemented(CSVFileRepository csvFileRepository) {
        this.csvFileRepository = csvFileRepository;
    }

    @Override
    public CompletableFuture<CSVFile> uploadCSVFile(MultipartFile file) throws IOException {
        CSVFile csvFile = new CSVFile();

        csvFile.setName(file.getOriginalFilename());
        csvFile.setPath(defaultPath + "\\" + csvFile.getName());
        csvFile.setSize(file.getSize());

        file.transferTo(new File(csvFile.getPath()));

        return CompletableFuture.completedFuture(csvFileRepository.save(csvFile));
    }

    @Override
    public CompletableFuture<CSVFile> getCSVFileById(Long id) {
        return CompletableFuture.completedFuture(csvFileRepository.findById(id).orElse(null));
    }

    @Override
    public CompletableFuture<List<CSVFile>> getAllCSVFiles() {
        return CompletableFuture.completedFuture(csvFileRepository.findAll());
    }

    @Override
    public CompletableFuture<CSVFile> deleteCSVFileById(Long id) {
        File file = new File(defaultPath + csvFileRepository.findById(id).get().getName());
        file.delete();

        CSVFile csvFile = csvFileRepository.findById(id).get();
        csvFileRepository.deleteById(id);

        return CompletableFuture.completedFuture(csvFile);
    }

    @Override
    public CompletableFuture<List<String>> getAllColumnNames(CSVFile file) {
        List<String> columnNames = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(file.getPath()))) {
            String[] headers = reader.readNext();

            if (headers != null) {
                for (String header : headers) {
                    columnNames.add(header.trim());
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(columnNames);
    }

    @Override
    public CompletableFuture<List<String>> getAllIndexesFromFile(Long id) {
        CSVFile file = csvFileRepository.findById(id).orElse(null);

        List<String> indexes = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(file.getPath()))) {
            List<String[]> rows = reader.readAll();

            boolean hasIndex = hasIndexOrTimeColumn(rows);

            if (hasIndex) {
                for (String[] row : rows) {
                    indexes.add(row[0]);
                }
            } else {
                for (int i = 0; i < rows.size(); i++) {
                    indexes.add(String.valueOf(i));
                }
            }

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        indexes.remove(0);
        return CompletableFuture.completedFuture(indexes);
    }

    private boolean hasIndexOrTimeColumn(List<String[]> rows) {
        String firstColumnName = rows.isEmpty() ? null : rows.get(0)[0];

        if(firstColumnName == "" || firstColumnName.toLowerCase().contains("index") || firstColumnName.toLowerCase().contains("time"))
        {
            int contor = 0;

            for (String[] row : rows) {
                if (isNumericOrDate(row[0])) {
                    contor++;
                }
            }
            if(contor == rows.size() - 1)
                return true;
        }

        return false;
    }
    private boolean isNumericOrDate(String input) {
        IntegerValidator integerValidator = IntegerValidator.getInstance();
        DateValidator dateValidator = DateValidator.getInstance();

        if (integerValidator.isValid(input)) { return true; }

        if (dateValidator.isValid(input, "yyyy-MM-dd HH:mm:ss")) { return true; }

        if (dateValidator.isValid(input, "yyyy/MM/dd HH:mm:ss")) { return true; }

        if (dateValidator.isValid(input, "dd-MM-yyyy HH:mm:ss")) { return  true; }

        if (dateValidator.isValid(input, "dd/MM/yyyy HH:mm:ss")) { return  true; }


        return false;
    }
}
