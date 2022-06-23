package ai.salmonbrain.experiment;

public interface Partitioner<T> {

    int getPartition(T key, int partitionsNum);
}
