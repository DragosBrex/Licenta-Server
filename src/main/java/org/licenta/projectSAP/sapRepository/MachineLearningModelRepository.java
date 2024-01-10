package org.licenta.projectSAP.sapRepository;

import org.licenta.projectSAP.sapRepository.entity.MachineLearningModel;
import org.licenta.projectSAP.sapRepository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineLearningModelRepository extends JpaRepository<MachineLearningModel, Long> {
    List<MachineLearningModel> findByUser(User user);
    MachineLearningModel findByName(String modelName);
    void deleteMachineLearningModelByName(String modelName);
}
