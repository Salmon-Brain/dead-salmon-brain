package com.salmonbrain.admin.repository;

import com.salmonbrain.admin.model.Experiment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExperimentRepository extends PagingAndSortingRepository<Experiment, Long>, CustomExperimentRepository {
    Page<Experiment> findAllByExpUidContainingIgnoreCase(String expUid, Pageable pageable);
}
