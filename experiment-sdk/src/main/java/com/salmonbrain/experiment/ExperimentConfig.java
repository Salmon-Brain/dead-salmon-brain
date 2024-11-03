package com.salmonbrain.experiment;

public interface ExperimentConfig {
    String name();

    int startPartition();

    int endPartition();

    int modulo();

    int variant();
}
