package ai.salmonbrain.admin.service;

import ai.salmonbrain.admin.dto.ExperimentDto;
import ai.salmonbrain.admin.model.Experiment;
import ai.salmonbrain.admin.model.ExperimentMetricData;
import ai.salmonbrain.admin.repository.ExperimentRepository;
import ai.salmonbrain.admin.repository.PageUtils;
import ai.salmonbrain.experiment.api.dto.ReportDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperimentService {
    private final ExperimentRepository repository;
    private final ModelMapper modelMapper;

    @Autowired
    public ExperimentService(ExperimentRepository repository) {
        this.repository = repository;
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    }

    public ResponseEntity<List<ExperimentDto>> getExperiments(String sort,
                                                              String order,
                                                              Integer start,
                                                              Integer end) {
        PageRequest pr = PageUtils.of(start, end, sort, order);
        Page<Experiment> page = repository.findAll(pr);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", Long.toString(page.getTotalElements()));
        List<ExperimentDto> list = page.getContent().stream()
                .map(e -> modelMapper.map(e, ExperimentDto.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(list, headers, HttpStatus.OK);
    }

    public ResponseEntity<ExperimentDto> getExperiment(Long id) {
        Experiment experiment = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        return new ResponseEntity<>(modelMapper.map(experiment, ExperimentDto.class), HttpStatus.OK);
    }

    public void updateReport(ReportDto report) {
        ExperimentMetricData metricData = modelMapper.map(report, ExperimentMetricData.class);
        repository.addStatToExperiment(report.getExperimentUid(), metricData);
    }
}
