package ai.salmonbrain.admin.service;

import ai.salmonbrain.admin.dto.ExperimentDto;
import ai.salmonbrain.admin.dto.ExperimentsPageDto;
import ai.salmonbrain.admin.model.Experiment;
import ai.salmonbrain.admin.model.ExperimentMetricData;
import ai.salmonbrain.admin.repository.ExperimentRepository;
import ai.salmonbrain.experiment.api.dto.ReportDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public ExperimentService(ExperimentRepository repository,
                             ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<ExperimentsPageDto> getExperiments(String filter,
                                                             String sort,
                                                             String order,
                                                             Integer pageNumber,
                                                             Integer pageSize) {

        PageRequest pr = PageRequest.of(pageNumber, pageSize, Sort.Direction.valueOf(order), sort);
        Page<Experiment> page = repository.findAllByExpUidContainingIgnoreCase(filter, pr);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", Long.toString(page.getTotalElements()));
        List<ExperimentDto> list = page.getContent().stream()
                .map(e -> modelMapper.map(e, ExperimentDto.class))
                .collect(Collectors.toList());
        ExperimentsPageDto result = new ExperimentsPageDto();
        result.setExperiments(list);
        result.setTotalCount(page.getTotalElements());
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
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
