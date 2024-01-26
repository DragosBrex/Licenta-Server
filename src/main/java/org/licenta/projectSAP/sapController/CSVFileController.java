package org.licenta.projectSAP.sapController;

import org.licenta.projectSAP.sapRepository.entity.CSVFile;
import org.licenta.projectSAP.sapService.CSVFileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "*")
public class CSVFileController {

    private final CSVFileService csvFileService;

    public CSVFileController(CSVFileService csvFileService) {
        this.csvFileService = csvFileService;
    }

    @PostMapping("/upload")
    public DeferredResult<ResponseEntity<CSVFile>> uploadCSVFile(@RequestParam("file") MultipartFile file) throws IOException {
        DeferredResult<ResponseEntity<CSVFile>> deferredResult = new DeferredResult<>();

        csvFileService.uploadCSVFile(file)
                .whenComplete((csvFile, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    } else {
                        deferredResult.setResult(ResponseEntity.status(HttpStatus.CREATED).body(csvFile));
                    }
                });

        return deferredResult;
    }

    @GetMapping("/{id}")
    public DeferredResult<ResponseEntity<CSVFile>> getCSVFileById(@PathVariable Long id) {
        DeferredResult<ResponseEntity<CSVFile>> deferredResult = new DeferredResult<>();

        csvFileService.getCSVFileById(id)
                .whenComplete((csvFile, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(ResponseEntity.notFound().build());
                    } else if (csvFile != null) {
                        deferredResult.setResult(ResponseEntity.ok(csvFile));
                    } else {
                        deferredResult.setErrorResult(ResponseEntity.notFound().build());
                    }
                });

        return deferredResult;
    }

    @GetMapping("/all")
    public DeferredResult<List<CSVFile>> getAllCSVFiles() {
        DeferredResult<List<CSVFile>> deferredResult = new DeferredResult<>();

        csvFileService.getAllCSVFiles()
                .whenComplete((csvFiles, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(Collections.emptyList());
                    } else {
                        deferredResult.setResult(csvFiles);
                    }
                });

        return deferredResult;
    }

    @DeleteMapping("/{id}")
    public DeferredResult<ResponseEntity<CSVFile>> deleteCSVFileById(@PathVariable Long id) {
        DeferredResult<ResponseEntity<CSVFile>> deferredResult = new DeferredResult<>();

        csvFileService.getCSVFileById(id)
                .whenComplete((csvFile, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(ResponseEntity.notFound().build());
                    } else if (csvFile != null) {
                        csvFileService.deleteCSVFileById(id)
                                .whenComplete((result, deleteThrowable) -> {
                                    if (deleteThrowable != null) {
                                        deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                                    } else {
                                        deferredResult.setResult(ResponseEntity.noContent().build());
                                    }
                                });
                    } else {
                        deferredResult.setErrorResult(ResponseEntity.notFound().build());
                    }
                });

        return deferredResult;
    }

    @GetMapping("/columns/{id}")
    public DeferredResult<ResponseEntity<List<String>>> getAllColumnNames(@PathVariable Long id) {
        DeferredResult<ResponseEntity<List<String>>> deferredResult = new DeferredResult<>();

        csvFileService.getCSVFileById(id)
                .thenApply(csvFile -> {
                    if (csvFile != null) {
                        try {
                            List<String> columnNames = csvFileService.getAllColumnNames(csvFile).get();
                            return ResponseEntity.ok(columnNames);
                        } catch (InterruptedException | ExecutionException e) {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                        }
                    } else {
                        return ResponseEntity.notFound().build();
                    }
                })
                .whenComplete((result, throwable) -> deferredResult.setResult((ResponseEntity<List<String>>) result));

        return deferredResult;
    }

    @GetMapping("/indexes/{id}")
    public DeferredResult<ResponseEntity<List<String>>> getAllIndexesFromFile(@PathVariable Long id) {
        DeferredResult<ResponseEntity<List<String>>> deferredResult = new DeferredResult<>();

        csvFileService.getAllIndexesFromFile(id)
                .whenComplete((indexes, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(ResponseEntity.notFound().build());
                    } else {
                        deferredResult.setResult(ResponseEntity.ok(indexes));
                    }
                });

        return deferredResult;
    }

    @GetMapping("/column/{fileId}/{columnName}")
    public DeferredResult<ResponseEntity<List<String>>> getColumnByName(@PathVariable Long fileId, @PathVariable String columnName) {
        DeferredResult<ResponseEntity<List<String>>> deferredResult = new DeferredResult<>();

        csvFileService.getColumnByName(fileId, columnName)
                .whenComplete((indexes, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(ResponseEntity.notFound().build());
                    } else {
                        deferredResult.setResult(ResponseEntity.ok(indexes));
                    }
                });

        return deferredResult;
    }
}
