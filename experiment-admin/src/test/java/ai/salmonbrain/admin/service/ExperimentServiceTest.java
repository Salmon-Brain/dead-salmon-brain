package ai.salmonbrain.admin.service;

import ai.salmonbrain.admin.dto.ExperimentDto;
import ai.salmonbrain.admin.dto.ExperimentMetricDataDto;
import ai.salmonbrain.admin.model.Experiment;
import ai.salmonbrain.admin.model.ExperimentMetricData;
import ai.salmonbrain.experiment.api.dto.ReportDto;
import ai.salmonbrain.experiment.api.dto.StatResultDto;
import ai.salmonbrain.experiment.api.dto.StatisticsDataDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.testng.Assert;

import java.sql.Timestamp;
import java.util.Collections;

public class ExperimentServiceTest {

    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapperFactory().modelMapper();
    }

    @Test
    public void report() {

        ReportDto dto = new ReportDto();
        dto.setExperimentUid("expUid");
        dto.setAdditive(true);
        dto.setMetricName("m");
        dto.setMetricSource("s");
        dto.setCategoryName("categoryName");
        dto.setCategoryValue("categoryValue");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        dto.setTimestamp(timestamp);
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

        Assert.assertEquals(metricData.isAdditive(), true);
        Assert.assertEquals(metricData.getMetricName(), "m");
        Assert.assertEquals(metricData.getMetricSource(), "s");
        Assert.assertEquals(metricData.getCategoryName(), "categoryName");
        Assert.assertEquals(metricData.getCategoryValue(), "categoryValue");
        Assert.assertEquals(metricData.getTimestamp(), timestamp);

        Assert.assertEquals(metricData.getAlpha(), 1);
        Assert.assertEquals(metricData.getBeta(), 2);
        Assert.assertEquals(metricData.getTestType(), "type");
        Assert.assertEquals(metricData.getControlSize(), 100);
        Assert.assertEquals(metricData.getTreatmentSize(), 100);
        Assert.assertEquals(metricData.isAdditive(), true);

        Assert.assertEquals(metricData.getCentralTendencyType(), "c");
        Assert.assertEquals(metricData.getControlCentralTendency(), 1);
        Assert.assertEquals(metricData.getControlVariance(), 2);
        Assert.assertEquals(metricData.getStatistic(), 3);
        Assert.assertEquals(metricData.getPValue(), 4D);
        Assert.assertEquals(metricData.getRequiredSampleSizeByVariant(), 5);
        Assert.assertEquals(metricData.getTreatmentCentralTendency(), 6);
        Assert.assertEquals(metricData.getTreatmentVariance(), 7);
        Assert.assertEquals(metricData.getPercentageLeft(), 8);
        Assert.assertEquals(metricData.getPercentageRight(), 8);
    }

    @Test
    public void experiment() {
        Experiment experiment = new Experiment();
        experiment.setExpUid("uuid");
        experiment.setId(1L);
        Timestamp timestamp = new Timestamp(1234L);
        experiment.setTimestamp(timestamp);
        ExperimentMetricData md = new ExperimentMetricData();
        md.setMetricName("m");
        md.setTimestamp(timestamp);
        md.setCategoryName("cn");
        md.setCategoryValue("cm");
        md.setSrm(true);
        md.setAdditive(true);
        md.setControlSize(1);
        md.setTreatmentSize(2);
        md.setTestType("tt");
        md.setAlpha(0.5);
        md.setBeta(0.1);
        md.setRequiredSampleSizeByVariant(100);
        md.setControlCentralTendency(200);
        md.setTreatmentCentralTendency(200);
        md.setControlVariance(1D);
        md.setTreatmentVariance(2D);
        md.setPercentageLeft(3D);
        md.setPercentageRight(4D);
        md.setCentralTendencyType("ct");

        experiment.setMetricData(Collections.singletonList(md));

        ExperimentDto dto = modelMapper.map(experiment, ExperimentDto.class);

        Assert.assertEquals(experiment.getId(), dto.getId());
        Assert.assertEquals(experiment.getExpUid(), dto.getExpUid());
        Assert.assertEquals(experiment.getTimestamp(), dto.getTimestamp());
        Assert.assertEquals(1, dto.getMetricData().size());
        ExperimentMetricDataDto first = dto.getMetricData().get(0);

        Assert.assertEquals(md.getMetricName(), first.getMetricName());
        Assert.assertEquals(md.getTimestamp(), first.getTimestamp());
        Assert.assertEquals(md.getCategoryName(), first.getStatisticsData().getCategoryName());
        Assert.assertEquals(md.getCategoryValue(), first.getStatisticsData().getCategoryValue());
        Assert.assertEquals(md.isSrm(), first.getStatisticsData().isSrm());
        Assert.assertEquals(md.isAdditive(), first.getStatisticsData().isAdditive());
        Assert.assertEquals(md.getControlSize(), first.getStatisticsData().getControlSize());
        Assert.assertEquals(md.getTreatmentSize(), first.getStatisticsData().getTreatmentSize());
        Assert.assertEquals(md.getTestType(), first.getStatisticsData().getTestType());
        Assert.assertEquals(md.getAlpha(), first.getStatisticsData().getAlpha());
        Assert.assertEquals(md.getBeta(), first.getStatisticsData().getBeta());
        Assert.assertEquals(md.getRequiredSampleSizeByVariant(), first.getStatisticsData().getStatResult().getRequiredSampleSizeByVariant());
        Assert.assertEquals(md.getControlCentralTendency(), first.getStatisticsData().getStatResult().getControlCentralTendency());
        Assert.assertEquals(md.getTreatmentCentralTendency(), first.getStatisticsData().getStatResult().getTreatmentCentralTendency());
        Assert.assertEquals(md.getControlVariance(), first.getStatisticsData().getStatResult().getControlVariance());
        Assert.assertEquals(md.getTreatmentVariance(), first.getStatisticsData().getStatResult().getTreatmentVariance());
        Assert.assertEquals(md.getPercentageLeft(), first.getStatisticsData().getStatResult().getPercentageLeft());
        Assert.assertEquals(md.getPercentageRight(), first.getStatisticsData().getStatResult().getPercentageRight());
        Assert.assertEquals(md.getCentralTendencyType(), first.getStatisticsData().getStatResult().getCentralTendencyType());
    }
}