package ai.salmonbrain.experiment.api.dto;

import java.sql.Timestamp;
import java.util.Objects;

public class ReportDto {
    private String experimentUid;
    private String metricName;
    private String metricSource;
    private boolean isAdditive;
    private StatisticsDataDto statisticsData;
    private Timestamp ts;

    public String getExperimentUid() {
        return experimentUid;
    }

    public void setExperimentUid(String experimentUid) {
        this.experimentUid = experimentUid;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricSource() {
        return metricSource;
    }

    public void setMetricSource(String metricSource) {
        this.metricSource = metricSource;
    }

    public boolean isAdditive() {
        return isAdditive;
    }

    public void setAdditive(boolean additive) {
        isAdditive = additive;
    }

    public StatisticsDataDto getStatisticsData() {
        return statisticsData;
    }

    public void setStatisticsData(StatisticsDataDto statisticsData) {
        this.statisticsData = statisticsData;
    }

    public Timestamp getTs() {
        return ts;
    }

    public void setTs(Timestamp ts) {
        this.ts = ts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportDto reportDto = (ReportDto) o;
        return isAdditive == reportDto.isAdditive && Objects.equals(experimentUid, reportDto.experimentUid) && Objects.equals(metricName, reportDto.metricName) && Objects.equals(metricSource, reportDto.metricSource) && Objects.equals(statisticsData, reportDto.statisticsData) && Objects.equals(ts, reportDto.ts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(experimentUid, metricName, metricSource, isAdditive, statisticsData, ts);
    }

    @Override
    public String toString() {
        return "ReportDto{" +
                "experimentUid='" + experimentUid + '\'' +
                ", metricName='" + metricName + '\'' +
                ", metricSource='" + metricSource + '\'' +
                ", isAdditive='" + isAdditive + '\'' +
                ", statisticsData=" + statisticsData +
                ", ts=" + ts +
                '}';
    }
}
