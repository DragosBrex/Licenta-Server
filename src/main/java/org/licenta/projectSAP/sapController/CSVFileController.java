package org.licenta.projectSAP.sapController;

import org.licenta.projectSAP.sapRepository.entity.CSVFile;
import org.licenta.projectSAP.sapService.CSVFileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "*")
public class CSVFileController {
    private final CSVFileService csvFileService;

    public CSVFileController(CSVFileService csvFileService) {
        this.csvFileService = csvFileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<CSVFile> uploadCSVFile(@RequestParam("file") MultipartFile file) {
        try {
            CSVFile csvFile = csvFileService.uploadCSVFile(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(csvFile);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CSVFile> getCSVFileById(@PathVariable Long id) {
        CSVFile csvFile = csvFileService.getCSVFileById(id);
        if (csvFile != null) {
            return ResponseEntity.ok(csvFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public List<CSVFile> getAllCSVFiles() {
        return csvFileService.getAllCSVFiles();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCSVFileById(@PathVariable Long id) {
        CSVFile csvFile = csvFileService.getCSVFileById(id);
        if (csvFile != null) {
            csvFileService.deleteCSVFileById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/columns/{id}")
    public ResponseEntity<List<String>> getAllColumnNames(@PathVariable Long id) {
        CSVFile csvFile = csvFileService.getCSVFileById(id);
        if (csvFile != null) {
            List<String> columnNames = csvFileService.getAllColumnNames(csvFile);
            return ResponseEntity.ok(columnNames);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/indexes/{id}")
    public ResponseEntity<List<String>> getAllIndexesFromFile(@PathVariable Long id) {
        if (id != null) {
            List<String> indexes = csvFileService.getAllIndexesFromFile(id);
            return ResponseEntity.ok(indexes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
