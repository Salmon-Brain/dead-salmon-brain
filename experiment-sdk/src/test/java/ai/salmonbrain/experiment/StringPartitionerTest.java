package ai.salmonbrain.experiment;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class StringPartitionerTest {
    private final StringPartitioner partitioner = new StringPartitioner();

    @Test
    public void distributionTest() {
        int partitionsNum = 1024;
        int keys = 1_000_000;
        int[] counts = new int[partitionsNum];
        for (int i = 0; i < keys; i++) {
            String key = UUID.randomUUID().toString();
            int partition = partitioner.getPartition(key, partitionsNum);
            counts[partition]++;
        }

        double expected = 1D * keys / partitionsNum;
        // max 20% skew
        double sigma = expected * 0.2;
        for (int i = 0; i < partitionsNum; i++) {
            Assert.assertEquals(counts[i], expected, sigma);
        }
    }
}