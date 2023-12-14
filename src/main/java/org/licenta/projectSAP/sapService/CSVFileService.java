package org.licenta.projectSAP.sapService;

import org.licenta.projectSAP.sapRepository.entity.CSVFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CSVFileService {
    CSVFile uploadCSVFile(MultipartFile file) throws IOException;

    CSVFile getCSVFileById(Long id);

    List<CSVFile> getAllCSVFiles();

    void deleteCSVFileById(Long id);

    List<String> getAllColumnNames(CSVFile file);
}
