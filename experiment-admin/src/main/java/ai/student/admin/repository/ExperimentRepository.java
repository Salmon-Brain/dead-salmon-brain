package ai.student.admin.repository;

import ai.student.admin.model.Experiment;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExperimentRepository extends PagingAndSortingRepository<Experiment, Long>, CustomExperimentRepository {

}
