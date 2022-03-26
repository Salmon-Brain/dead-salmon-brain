package ai.salmonbrain.admin.service;

import ai.salmonbrain.admin.dto.ExperimentDto;
import ai.salmonbrain.admin.dto.ExperimentMetricDataDto;
import ai.salmonbrain.admin.dto.StatResultDto;
import ai.salmonbrain.admin.dto.StatisticsDataDto;
import ai.salmonbrain.admin.model.Experiment;
import ai.salmonbrain.admin.model.ExperimentMetricData;
import ai.salmonbrain.experiment.api.dto.ReportDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperFactory {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        Converter<ReportDto, ExperimentMetricData> reportDtoConverter = context -> {
            ExperimentMetricData result = new ExperimentMetricData();
            ReportDto source = context.getSource();
            result.setMetricName(source.getMetricName());
            result.setMetricSource(source.getMetricSource());
            result.setCategoryName(source.getCategoryName());
            result.setCategoryValue(source.getCategoryValue());
            result.setAdditive(source.isAdditive());
            result.setTimestamp(source.getTimestamp());

            result.setSrm(source.getStatisticsData().isSrm());
            result.setControlSize(source.getStatisticsData().getControlSize());
            result.setTreatmentSize(source.getStatisticsData().getTreatmentSize());
            result.setTestType(source.getStatisticsData().getTestType());
            result.setAlpha(source.getStatisticsData().getAlpha());
            result.setBeta(source.getStatisticsData().getBeta());

            result.setStatistic(source.getStatisticsData().getStatResult().getStatistic());
            result.setPValue(source.getStatisticsData().getStatResult().getPValue());
            result.setRequiredSampleSizeByVariant(source.getStatisticsData().getStatResult().getRequiredSampleSizeByVariant());
            result.setControlCentralTendency(source.getStatisticsData().getStatResult().getControlCentralTendency());
            result.setTreatmentCentralTendency(source.getStatisticsData().getStatResult().getTreatmentCentralTendency());
            result.setControlVariance(source.getStatisticsData().getStatResult().getControlVariance());
            result.setTreatmentVariance(source.getStatisticsData().getStatResult().getTreatmentVariance());
            result.setPercentageLeft(source.getStatisticsData().getStatResult().getPercentageLeft());
            result.setPercentageRight(source.getStatisticsData().getStatResult().getPercentageRight());
            result.setCentralTendencyType(source.getStatisticsData().getStatResult().getCentralTendencyType());

            return result;
        };

        Converter<Experiment, ExperimentDto> experimentDtoConverter = context -> {
            ExperimentDto result = new ExperimentDto();
            Experiment source = context.getSource();
            result.setId(source.getId());
            result.setExpUid(source.getExpUid());
            result.setTimestamp(source.getTimestamp());
            List<ExperimentMetricDataDto> list = Optional.ofNullable(source.getMetricData())
                    .map(v -> v.stream()
                            .map(m -> {
                                ExperimentMetricDataDto r = new ExperimentMetricDataDto();
                                r.setMetricName(m.getMetricName());
                                r.setTimestamp(m.getTimestamp());
                                StatisticsDataDto sd = new StatisticsDataDto();

                                StatResultDto sr = new StatResultDto();
                                sr.setStatistic(m.getStatistic());
                                sr.setPValue(m.getPValue());
                                sr.setRequiredSampleSizeByVariant(m.getRequiredSampleSizeByVariant());
                                sr.setControlCentralTendency(m.getControlCentralTendency());
                                sr.setTreatmentCentralTendency(m.getTreatmentCentralTendency());
                                sr.setControlVariance(m.getControlVariance());
                                sr.setTreatmentVariance(m.getTreatmentVariance());
                                sr.setPercentageLeft(m.getPercentageLeft());
                                sr.setPercentageRight(m.getPercentageRight());
                                sr.setCentralTendencyType(m.getCentralTendencyType());
                                sd.setStatResult(sr);

                                sd.setSrm(m.isSrm());
                                sd.setControlSize(m.getControlSize());
                                sd.setTreatmentSize(m.getTreatmentSize());
                                sd.setTestType(m.getTestType());
                                sd.setAdditive(m.isAdditive());
                                sd.setAlpha(m.getAlpha());
                                sd.setBeta(m.getBeta());
                                sd.setCategoryName(m.getCategoryName());
                                sd.setCategoryValue(m.getCategoryValue());
                                sd.setMetricSource(m.getMetricSource());
                                r.setStatisticsData(sd);
                                return r;
                            })
                            .collect(Collectors.toList()))
                    .orElse(null);
            result.setMetricData(list);
            return result;
        };

        modelMapper.createTypeMap(Experiment.class, ExperimentDto.class)
                .setConverter(experimentDtoConverter);
        modelMapper.createTypeMap(ReportDto.class, ExperimentMetricData.class)
                .setConverter(reportDtoConverter);
        return modelMapper;
    }
}
