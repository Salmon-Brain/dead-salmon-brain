package ai.salmonbrain.admin.repository;

import ai.salmonbrain.admin.model.Experiment;
import ai.salmonbrain.admin.model.ExperimentMetricData;
import ai.salmonbrain.admin.model.StatResult;
import ai.salmonbrain.admin.model.StatisticsData;
import org.assertj.core.api.HamcrestCondition;
import org.assertj.core.condition.AnyOf;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class ExperimentRepositoryTest {

    private final AtomicLong ts = new AtomicLong(0);

    @Autowired
    private ExperimentRepository repository;

    @Test
    public void findOrCreate() {
        Experiment exp1 = repository.findOrCreate("exp1");
        Experiment exp2 = repository.findOrCreate("exp2");
        Experiment exp3 = repository.findOrCreate("exp1");

        assertThat(exp1).isEqualTo(exp3);
        assertThat(exp1).isNotEqualTo(exp2);
    }

    @Test
    public void paging() {
        for (int i = 0; i < 100; i++) {
            repository.findOrCreate("exp" + i);
        }

        Page<Experiment> experiments = repository.findAll(pr(0, 5, "id", "ASC"));
        assertThat(experiments.getTotalElements()).isEqualTo(100L);

        assertThat(experiments.stream().map(Experiment::getExpUid).collect(Collectors.toList()))
                .isEqualTo(Arrays.asList("exp0", "exp1", "exp2", "exp3", "exp4"));

        experiments = repository.findAll(pr(1, 5, "id", "ASC"));
        assertThat(experiments.stream().map(Experiment::getExpUid).collect(Collectors.toList()))
                .isEqualTo(Arrays.asList("exp5", "exp6", "exp7", "exp8", "exp9"));

        experiments = repository.findAll(pr(1, 5, "id", "DESC"));
        assertThat(experiments.stream().map(Experiment::getExpUid).collect(Collectors.toList()))
                .isEqualTo(Arrays.asList("exp94", "exp93", "exp92", "exp91", "exp90"));
    }

    @Test
    public void filterByExpUid() {
            repository.findOrCreate("someDogExp");
            repository.findOrCreate("otherCatExp");
            repository.findOrCreate("bestFishExp");

        Page<Experiment> fish = repository.findAllByExpUidContainingIgnoreCase("fish", pr(0, 5, "id", "ASC"));
        assertThat(fish.getTotalElements()).isEqualTo(1);
        assertThat(
                fish.stream().map(Experiment::getExpUid).collect(Collectors.joining(","))
        ).isEqualTo("bestFishExp");

        Page<Experiment> cat = repository.findAllByExpUidContainingIgnoreCase("cat", pr(0, 5, "id", "ASC"));
        assertThat(fish.getTotalElements()).isEqualTo(1);
        assertThat(
                cat.stream().map(Experiment::getExpUid).collect(Collectors.joining(","))
        ).isEqualTo("otherCatExp");

        Page<Experiment> all = repository.findAllByExpUidContainingIgnoreCase("exp", pr(0, 5, "id", "ASC"));
        assertThat(fish.getTotalElements()).isEqualTo(1);
        assertThat(
                all.stream()
                        .map(Experiment::getExpUid).sorted()
                        .collect(Collectors.joining(","))
        ).isEqualTo("bestFishExp,otherCatExp,someDogExp");

        Page<Experiment> noFilter = repository.findAllByExpUidContainingIgnoreCase("", pr(0, 5, "id", "ASC"));
        assertThat(fish.getTotalElements()).isEqualTo(1);
        assertThat(
                noFilter.stream()
                        .map(Experiment::getExpUid).sorted()
                        .collect(Collectors.joining(","))
        ).isEqualTo("bestFishExp,otherCatExp,someDogExp");

   }

    @Test
    public void saveMetricData() {
        Experiment exp1 = repository.findOrCreate("exp1");
        // no metrics
        assertThat(exp1.getMetricData()).is(AnyOf.anyOf(
                new HamcrestCondition<>(IsNull.nullValue()),
                new HamcrestCondition<>(IsCollectionWithSize.hasSize(0))
        ));

        ExperimentMetricData md1;
        ExperimentMetricData md2;
        ExperimentMetricData md3;
        repository.addStatToExperiment("exp1", md1 = md());
        repository.addStatToExperiment("exp1", md2 = md());
        repository.addStatToExperiment("exp1", md3 = md());

        Experiment exp2 = repository.findOrCreate("exp1");
        assertThat(exp2.getMetricData()).isNotNull();
        assertThat(exp2.getMetricData().size()).isEqualTo(3);
        assertThat(exp2.getMetricData().get(0).getTs()).isEqualTo(md1.getTs());
        assertThat(exp2.getMetricData().get(1).getTs()).isEqualTo(md2.getTs());
        assertThat(exp2.getMetricData().get(2).getTs()).isEqualTo(md3.getTs());
    }

    private ExperimentMetricData md() {
        return new ExperimentMetricData("m",
                new Timestamp(1000 * ts.incrementAndGet()),
                new StatisticsData(
                        new StatResult(
                                1,
                                0.05,
                                500,
                                1,
                                1,
                                0.01,
                                0.01,
                                0.95,
                                1.05,
                                "type"
                        ),
                        false,
                        1000,
                        1000,
                        0.1,
                        0.2,
                        "test",
                        "source",
                        true
                ));
    }

    private static PageRequest pr(int pageNumber, int pageSize, String sort, String order) {
        return PageRequest.of(pageNumber, pageSize, Sort.Direction.valueOf(order), sort);
    }
}
