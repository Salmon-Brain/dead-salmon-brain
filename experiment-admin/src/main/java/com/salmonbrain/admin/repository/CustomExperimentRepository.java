package com.salmonbrain.admin.repository;

import com.salmonbrain.admin.model.Experiment;
import com.salmonbrain.admin.model.ExperimentMetricData;

public interface CustomExperimentRepository {

    Experiment findOrCreate(String expUid);

    void addStatToExperiment(String expUid, ExperimentMetricData data);
}
