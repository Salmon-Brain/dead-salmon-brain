package ai.salmonbrain.admin.repository;

import ai.salmonbrain.admin.model.Experiment;
import ai.salmonbrain.admin.model.ExperimentMetricData;

public interface CustomExperimentRepository {

    Experiment findOrCreate(String expUid);

    void addStatToExperiment(String expUid, ExperimentMetricData data);
}
