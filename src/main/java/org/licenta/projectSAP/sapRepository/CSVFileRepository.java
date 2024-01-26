package org.licenta.projectSAP.sapRepository;

import org.licenta.projectSAP.sapRepository.entity.CSVFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CSVFileRepository extends JpaRepository<CSVFile, Long> {
    CSVFile findByName(String name);
}
