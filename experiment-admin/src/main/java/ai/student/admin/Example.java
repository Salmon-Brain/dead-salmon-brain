package ai.student.admin;

import ai.student.admin.model.ExperimentMetricData;
import ai.student.admin.model.StatResult;
import ai.student.admin.model.StatisticsData;
import ai.student.admin.repository.ExperimentMetricDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

@Component
public class Example {
    private final ExperimentMetricDataRepository repository;

    @Autowired
    public Example(ExperimentMetricDataRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        repository.save(new ExperimentMetricData(
                "exp1", "metric1", Timestamp.from(ZonedDateTime.now().toInstant()),
                new StatisticsData(
                new StatResult(1, 0, 10000, 10, 20,
                        10, 20, "TYPE"),
                false, 10000, 1000, "TEST","source1", true
        )
        ));
        repository.save(new ExperimentMetricData(
                "exp2", "metric3", Timestamp.from(ZonedDateTime.now().toInstant()),
                new StatisticsData(
                new StatResult(1, 0, 10000, 10, 20,
                        10, 20, "TYPE"),
                false, 10000, 1000, "TEST","source1", true
        )
        ));
        repository.save(new ExperimentMetricData(
                "exp3", "metric3", Timestamp.from(ZonedDateTime.now().toInstant()),
                new StatisticsData(
                new StatResult(1, 0, 10000, 10, 20,
                        10, 20, "TYPE"),
                false, 10000, 1000, "TEST","source1", true
        )
        ));


        System.out.println("By exp1 " + repository.findByExpUid("exp1"));
        System.out.println("All " + repository.findAll());
    }
}
