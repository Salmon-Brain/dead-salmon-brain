package ai.salmonbrain.admin;

import ai.salmonbrain.admin.model.ExperimentMetricData;
import ai.salmonbrain.admin.model.StatResult;
import ai.salmonbrain.admin.model.StatisticsData;
import ai.salmonbrain.admin.repository.CustomExperimentRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        List<Integer> fakeCounts = getFakeCounts();
        for (int expId = 0; expId < fakeCounts.get(0); expId++) {
            List<ExperimentMetricData> datas = new ArrayList<>(3 * 3 * 20);
            List<String> categoryValues = IntStream.rangeClosed(1, fakeCounts.get(1))
                    .mapToObj(v -> "categoryValue" + v)
                    .collect(Collectors.toList());
            for (String categoryValue : categoryValues) {
                for (int m = 0; m < fakeCounts.get(2); m++) {
                    for (int j = 0; j < fakeCounts.get(3); j++) {
                        datas.add(getMetricData(j, "metric_" + m, "category", categoryValue));
                    }
                }
            }
            repository.addStatToExperiment("fakeExp_" + expId, datas);
        }
    }

    private List<Integer> getFakeCounts() {
        String expsData = System.getenv("SALMON_BRAIN_FAKE_EXPS_DATA");
        if (expsData == null || expsData.isEmpty()) {
            expsData = System.getProperty("SALMON_BRAIN_FAKE_EXPS_DATA");
        }
        if (expsData == null || expsData.isEmpty()) {
            return Collections.emptyList();
        }
        String[] split = expsData.split(",");
        if (split.length != 4) {
            return Collections.emptyList();
        }
        return Arrays.stream(split).map(Integer::parseInt).collect(Collectors.toList());
    }

    private ExperimentMetricData getMetricData(int j, String metricName, String categoryName, String categoryValue) {

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
                                random.nextDouble(0, 2),
                                random.nextDouble(0, 2),
                                left,
                                right,
                                "central_tendency_type"),
                        false, 100, 100, 0.1, 0.2, "test_type",
                        "metric_source", categoryName, categoryValue, false
                )
        );
    }
}
