package ai.salmonbrain.experiment;

import ai.salmonbrain.client.hash.HashFunctions;

public class StringPartitioner implements Partitioner<String> {
    private static final int MURMUR_SEED = 0;

    @Override
    public int getPartition(String id, int partitionsNum) {
        int p = (int) (HashFunctions.murmur3(id, MURMUR_SEED) % partitionsNum);
        return p < 0 ? p + partitionsNum : p;
    }
}
