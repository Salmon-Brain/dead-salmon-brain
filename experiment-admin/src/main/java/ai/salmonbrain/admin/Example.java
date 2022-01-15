package ai.salmonbrain.admin;

import ai.salmonbrain.admin.model.Experiment;
import ai.salmonbrain.admin.model.ExperimentMetricData;
import ai.salmonbrain.admin.model.StatResult;
import ai.salmonbrain.admin.model.StatisticsData;
import ai.salmonbrain.admin.repository.CustomExperimentRepositoryImpl;
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
        for (int m = 0; m < 3; m++) {
            for (int i = 0; i < 100; i++) {
                Experiment experiment = repository.findOrCreate("fakeExp" + i);
                for (int j = 0; j < 20; j++) {
                    repository.addStatToExperiment(experiment.getExpUid(), getMetricData(j, "metric_" + m));
                }
            }
        }
    }

    private ExperimentMetricData getMetricData(int j, String metricName) {
        return new ExperimentMetricData(
                metricName,
                new Timestamp(System.currentTimeMillis() + j * 1000),
                new StatisticsData(
                        new StatResult(
                                ThreadLocalRandom.current().nextDouble(0, 10),
                                ThreadLocalRandom.current().nextDouble(0, 0.1),
                                3, 4, 5,
                                ThreadLocalRandom.current().nextDouble(0, 0.1),
                                ThreadLocalRandom.current().nextDouble(0, 0.1),
                                ThreadLocalRandom.current().nextDouble(0, 0.1),
                                ThreadLocalRandom.current().nextDouble(0, 0.1),
                                "central_tendency_type"),
                        false, 100, 100, 0.1, 0.2, "test_type",
                        "metric_source", false
                )
        );
    }
}
