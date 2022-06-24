package ai.salmonbrain.experiment;

import ai.salmonbrain.client.hash.HashFunction;

import java.util.List;

public class ExperimentConfigSelector<T, E extends ExperimentConfig> {
    private final Partitioner<T> partitioner;
    private final HashFunction<T> hashFunction;

    private final int partitionsNum;

    public ExperimentConfigSelector(Partitioner<T> partitioner,
                                    HashFunction<T> hashFunction,
                                    int partitionsNum) {
        if (partitionsNum < 1) {
            throw new IllegalArgumentException("Partitions num must be >= 1");
        }
        this.partitioner = partitioner;
        this.hashFunction = hashFunction;
        this.partitionsNum = partitionsNum;
    }

    public E select(T key, List<E> experiments) {
        int partition = partitioner.getPartition(key, partitionsNum);
        for (E experiment : experiments) {
            if (partition < experiment.startPartition() || partition > experiment.endPartition()) {
                continue;
            }
            int hash = (int) (hashFunction.getHash(key, experiment.name()) % experiment.modulo());
            if (hash < 0) {
                hash += experiment.modulo();
            }
            if (hash == experiment.variant()) {
                return experiment;
            }
        }
        return null;
    }
}
