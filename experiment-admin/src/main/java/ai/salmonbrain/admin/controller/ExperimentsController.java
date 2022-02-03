package ai.salmonbrain.admin.controller;

import ai.salmonbrain.admin.dto.ExperimentDto;
import ai.salmonbrain.admin.dto.ExperimentsPageDto;
import ai.salmonbrain.admin.service.ExperimentService;
import ai.salmonbrain.experiment.api.dto.ReportDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExperimentsController {
    private final ExperimentService service;

    @Autowired
    public ExperimentsController(ExperimentService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.POST, path = "report")
    @ResponseBody
    ResponseEntity<String> postReport(@RequestBody ReportDto report) {
        service.updateReport(report);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "experiments/{id}")
    @ResponseBody
    ResponseEntity<ExperimentDto> getExperiment(@PathVariable(value = "id") Long id) {
        return service.getExperiment(id);
    }

    @RequestMapping(method = RequestMethod.GET, path = "experiments")
    @ResponseBody
    ResponseEntity<ExperimentsPageDto> getExperiments(
            @RequestParam(value = "filter", required = false, defaultValue = "") String filter,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort,
            @RequestParam(value = "order", required = false, defaultValue = "DESC") String order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return service.getExperiments(filter, sort, order, pageNumber, pageSize);
    }
}
