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
    private static final long HOUR = 3600000L;
    private static final long DAY = 1000L * 60 * 60 * 24;
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

        ThreadLocalRandom random = ThreadLocalRandom.current();
        double left = random.nextDouble(0, 0.1);
        double right = left + random.nextDouble(0, 0.1);
        return new ExperimentMetricData(
                metricName,
                new Timestamp(System.currentTimeMillis() + j * DAY + random.nextLong(0, HOUR)),
                new StatisticsData(
                        new StatResult(
                                random.nextDouble(0, 10),
                                random.nextDouble(0, 0.1),
                                random.nextInt(10, 10000),
                                random.nextDouble(10, 20),
                                random.nextDouble(10, 20),
                                random.nextDouble(0, 0.1),
                                random.nextDouble(0, 0.1),
                                left,
                                right,
                                "central_tendency_type"),
                        false, 100, 100, 0.1, 0.2, "test_type",
                        "metric_source", false
                )
        );
    }
}
