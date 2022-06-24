package ai.salmonbrain.experiment;

import ai.salmonbrain.client.hash.HashFunction;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class ExperimentConfigSelectorTest {

    private static final int partsNum = 100;

    @Test
    public void configSelectTest() {
        ExpImpl def = new ExpImpl("default", 0, 99, 1, 0);
        ExpImpl exp1 = new ExpImpl("exp1", 0, 25, 2, 1);
        ExpImpl exp2 = new ExpImpl("exp2", 26, 50, 2, 1);
        ExpImpl exp3 = new ExpImpl("exp3", 51, 99, 10, 0);

        ExperimentConfigSelector<Long, ExpImpl> selector =
                new ExperimentConfigSelector<>(
                        new LongPartitioner(),
                        new DummyLongHash(),
                        partsNum
                );

        List<ExpImpl> list = Arrays.asList(exp1, exp2, exp3, def);

        Assert.assertEquals(selector.select(0L, list), def);
        Assert.assertEquals(selector.select(1L, list), exp1);
        Assert.assertEquals(selector.select(101L, list), exp1);
        Assert.assertEquals(selector.select(125101L, list), exp1);
        Assert.assertEquals(selector.select(2L, list), def);
        Assert.assertEquals(selector.select(102L, list), def);
        Assert.assertEquals(selector.select(103L, list), exp1);
        Assert.assertEquals(selector.select(104L, list), def);
        Assert.assertEquals(selector.select(105L, list), exp1);
        Assert.assertEquals(selector.select(125L, list), exp1);

        Assert.assertEquals(selector.select(126L, list), def);
        Assert.assertEquals(selector.select(127L, list), exp2);
        Assert.assertEquals(selector.select(1029L, list), exp2);
        Assert.assertEquals(selector.select(1515139L, list), exp2);

        Assert.assertEquals(selector.select(1051L, list), def);
        Assert.assertEquals(selector.select(1052L, list), def);
        Assert.assertEquals(selector.select(1053L, list), def);
        Assert.assertEquals(selector.select(1059L, list), def);
        Assert.assertEquals(selector.select(1060L, list), exp3);
        Assert.assertEquals(selector.select(1070L, list), exp3);
        Assert.assertEquals(selector.select(1080L, list), exp3);
        Assert.assertEquals(selector.select(1090L, list), exp3);
        Assert.assertEquals(selector.select(1000L, list), def);


        Map<ExpImpl, Integer> counts = LongStream.range(0L, 10_000L)
                .mapToObj(id -> selector.select(id, list))
                .collect(Collectors.groupingBy(e -> e, Collectors.summingInt(e -> 1)));

        Assert.assertEquals((int) counts.get(def), 7100);
        Assert.assertEquals((int) counts.get(exp1), 1300);
        Assert.assertEquals((int) counts.get(exp2), 1200);
        Assert.assertEquals((int) counts.get(exp3), 400);
    }

    private static class DummyLongHash implements HashFunction<Long> {
        @Override
        public long getHash(Long key, String salt) {
            return key;
        }
    }

    private static class ExpImpl implements ExperimentConfig {

        private final String name;
        private final int start;
        private final int end;
        private final int modulo;
        private final int variant;

        public ExpImpl(String name, int start, int end, int modulo, int variant) {
            this.name = name;
            this.start = start;
            this.end = end;
            this.modulo = modulo;
            this.variant = variant;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public int startPartition() {
            return start;
        }

        @Override
        public int endPartition() {
            return end;
        }

        @Override
        public int modulo() {
            return modulo;
        }

        @Override
        public int variant() {
            return variant;
        }

        @Override
        public String toString() {
            return "ExpImpl{" +
                    "name='" + name + '\'' +
                    ", start=" + start +
                    ", end=" + end +
                    ", modulo=" + modulo +
                    ", variant=" + variant +
                    '}';
        }
    }
}