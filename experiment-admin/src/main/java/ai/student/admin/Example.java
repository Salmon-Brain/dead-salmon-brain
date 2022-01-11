package ai.student.admin;

import ai.student.admin.model.Experiment;
import ai.student.admin.model.ExperimentMetricData;
import ai.student.admin.model.StatResult;
import ai.student.admin.model.StatisticsData;
import ai.student.admin.repository.CustomExperimentRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class Example {
    private final CustomExperimentRepositoryImpl repository;

    @Autowired
    public Example(CustomExperimentRepositoryImpl repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        if (!"true".equals(System.getProperty("ai.salmonbrain.init.fakes", "false"))) {
            return;
        }
        for (int i = 0; i < 100; i++) {
            Experiment experiment = repository.findOrCreate("fakeExp" + i);
            for (int j = 0; j < 20; j++) {
                repository.addStatToExperiment(experiment.getExpUid(), getMetricData(j));
            }
        }
    }

    private ExperimentMetricData getMetricData(int j) {
        return new ExperimentMetricData(
                "m1",
                new Timestamp(System.currentTimeMillis() + j * 1000),
                new StatisticsData(
                        new StatResult(
                                ThreadLocalRandom.current().nextDouble(0, 10),
                                ThreadLocalRandom.current().nextDouble(0, 0.1),
                                3, 4, 5, 6, 7, "c"),
                        false, 100, 100, "tt", "ms", false
                )
        );
    }
}
