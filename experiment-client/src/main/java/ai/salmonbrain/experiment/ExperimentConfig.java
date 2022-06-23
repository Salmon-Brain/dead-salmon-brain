package ai.salmonbrain.experiment;

public interface ExperimentConfig {
    String name();

    int startPartition();

    int endPartition();

    int modulo();

    int variant();
}
