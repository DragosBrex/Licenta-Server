package org.licenta.projectSAP.sapService;

import org.licenta.projectSAP.sapRepository.entity.CSVFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CSVFileService {
    @Async
    CompletableFuture<CSVFile> uploadCSVFile(MultipartFile file) throws IOException;

    @Async
    CompletableFuture<CSVFile> getCSVFileById(Long id);

    @Async
    CompletableFuture<List<CSVFile>> getAllCSVFiles();

    @Async
    CompletableFuture<CSVFile> deleteCSVFileById(Long id);

    @Async
    CompletableFuture<List<String>> getAllColumnNames(CSVFile file);

    @Async
    CompletableFuture<List<String>> getAllIndexesFromFile(Long id);

    @Async
    CompletableFuture<List<String>> getColumnByName(Long fileId, String columnName);
}
