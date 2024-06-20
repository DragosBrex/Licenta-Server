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

//    @Override
//    public CompletableFuture<List<String>> getAllColumnNames(CSVFile file) {
//        List<String> columnNames = new ArrayList<>();
//
//        try (CSVReader reader = new CSVReader(new FileReader(file.getPath()))) {
//            String[] headers = reader.readNext();
//
//            if (headers != null) {
//                for (String header : headers) {
//                    columnNames.add(header.trim());
//                }
//            }
//        } catch (IOException | CsvValidationException e) {
//            e.printStackTrace();
//        }
//
//        return CompletableFuture.completedFuture(columnNames);
//    }

    @Override
    public CompletableFuture<List<String>> getAllColumnNames(CSVFile file) {
        List<String> numericColumnNames = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(file.getPath()))) {
            String[] headers = reader.readNext();

            if (headers != null) {
                int numColumns = headers.length;
                boolean[] isNumericColumn = new boolean[numColumns];
                for (int i = 0; i < isNumericColumn.length; i++) {
                    isNumericColumn[i] = true;
                }

                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    for (int i = 0; i < numColumns; i++) {
                        try {
                            Double.parseDouble(nextLine[i].trim());
                        } catch (NumberFormatException e) {
                            isNumericColumn[i] = false;
                        }
                    }
                }

                for (int i = 0; i < numColumns; i++) {
                    if (isNumericColumn[i]) {
                        numericColumnNames.add(headers[i]);
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        return CompletableFuture.completedFuture((numericColumnNames));
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

        if(firstColumnName == "" || firstColumnName.toLowerCase().contains("index") ||
                firstColumnName.toLowerCase().contains("time"))
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

    @Override
    public CompletableFuture<List<String>> getColumnByName(Long fileId, String columnName) {
        CSVFile file = csvFileRepository.findById(fileId).get();

        List<String> values = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(file.getPath()))) {
            List<String[]> rows = reader.readAll();

            int columnIndex = -1;
            String[] header = rows.get(0);
            for (int i = 0; i < header.length; i++) {
                if (header[i].equals(columnName)) {
                    columnIndex = i;
                    break;
                }
            }

            if (columnIndex != -1) {
                for (int i = 1; i < rows.size(); i++) {
                    String[] row = rows.get(i);
                    if (columnIndex < row.length) {
                        values.add(row[columnIndex]);
                    }
                }
            } else {
                throw new IllegalArgumentException("Column with name '" + columnName + "' not found");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }

        return CompletableFuture.completedFuture(values);
    }
}
