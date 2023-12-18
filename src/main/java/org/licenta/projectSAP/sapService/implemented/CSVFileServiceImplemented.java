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

@Service
public class CSVFileServiceImplemented implements CSVFileService {
    private final CSVFileRepository csvFileRepository;

    private final String defaultPath = "C:\\Users\\drago\\Desktop\\Licenta Brisc Dragos-Nicolae\\Back-End\\FileStorage";

    public CSVFileServiceImplemented(CSVFileRepository csvFileRepository) {
        this.csvFileRepository = csvFileRepository;
    }

    @Override
    public CSVFile uploadCSVFile(MultipartFile file) throws IOException {
        CSVFile csvFile = new CSVFile();

        csvFile.setName(file.getOriginalFilename());
        csvFile.setPath(defaultPath + "\\" + csvFile.getName());
        csvFile.setSize(file.getSize());

        file.transferTo(new File(csvFile.getPath()));

        return csvFileRepository.save(csvFile);
    }

    @Override
    public CSVFile getCSVFileById(Long id) {
        return csvFileRepository.findById(id).orElse(null);
    }

    @Override
    public List<CSVFile> getAllCSVFiles() {
        return csvFileRepository.findAll();
    }

    @Override
    public void deleteCSVFileById(Long id) {
        File file = new File(defaultPath + csvFileRepository.findById(id).get().getName());
        file.delete();

        csvFileRepository.deleteById(id);
    }

    @Override
    public List<String> getAllColumnNames(CSVFile file) {
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

        return columnNames;
    }

    @Override
    public List<String> getAllIndexesFromFile(Long id) {
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
        return indexes;
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
