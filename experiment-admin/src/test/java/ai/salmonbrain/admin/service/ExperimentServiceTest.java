package ai.salmonbrain.admin.service;

import ai.salmonbrain.admin.model.ExperimentMetricData;
import ai.salmonbrain.experiment.api.dto.ReportDto;
import ai.salmonbrain.experiment.api.dto.StatResultDto;
import ai.salmonbrain.experiment.api.dto.StatisticsDataDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.sql.Timestamp;

class ExperimentServiceTest {
    @Test
    public void test() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

        ReportDto dto = new ReportDto();
        dto.setExperimentUid("expUid");
        dto.setAdditive(true);
        dto.setMetricName("m");
        dto.setMetricSource("s");
        dto.setTs(new Timestamp(System.currentTimeMillis()));
        StatisticsDataDto data = new StatisticsDataDto();
        dto.setStatisticsData(data);
        data.setAlpha(1);
        data.setBeta(2);
        data.setTestType("type");
        data.setControlSize(100);
        data.setTreatmentSize(100);
        data.setSrm(true);
        StatResultDto statResult = new StatResultDto();
        data.setStatResult(statResult);
        statResult.setCentralTendencyType("c");
        statResult.setControlCentralTendency(1);
        statResult.setControlVariance(2);
        statResult.setStatistic(3);
        statResult.setPValue(4D);
        statResult.setRequiredSampleSizeByVariant(5);
        statResult.setTreatmentCentralTendency(6);
        statResult.setTreatmentVariance(7);
        statResult.setPercentageLeft(8);
        statResult.setPercentageRight(8);

        ExperimentMetricData metricData = modelMapper.map(dto, ExperimentMetricData.class);
        System.out.println(metricData);

    }

}