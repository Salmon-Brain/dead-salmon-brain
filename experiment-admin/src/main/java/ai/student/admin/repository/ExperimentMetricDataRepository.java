package ai.student.admin.repository;

import ai.student.admin.model.ExperimentMetricData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExperimentMetricDataRepository extends CrudRepository<ExperimentMetricData,String> {
    @Query("SELECT u FROM ExperimentMetricData u WHERE u.expUid = ?1")
    List<ExperimentMetricData> findByExpUid(String experimentUid);

}
