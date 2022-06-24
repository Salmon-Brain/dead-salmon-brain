package ai.salmonbrain.experiment;

public class LongPartitioner implements Partitioner<Long> {

    @Override
    public int getPartition(Long id, int partitionsNum) {
        int p = (int) (id % partitionsNum);
        return p < 0 ? p + partitionsNum : p;
    }
}
