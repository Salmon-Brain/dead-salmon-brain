package ai.salmonbrain.admin.repository;

import ai.salmonbrain.admin.model.Experiment;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExperimentRepository extends PagingAndSortingRepository<Experiment, Long>, CustomExperimentRepository {

}
