package ai.student.admin.repository;

import ai.student.admin.model.Experiment;
import ai.student.admin.model.ExperimentMetricData;

public interface CustomExperimentRepository {

    Experiment findOrCreate(String expUid);

    void addStatToExperiment(String expUid, ExperimentMetricData data);
}
