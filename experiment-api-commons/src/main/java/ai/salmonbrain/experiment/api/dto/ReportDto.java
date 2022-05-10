package ai.salmonbrain.experiment.api.dto;

import java.sql.Timestamp;
import java.util.Objects;

public class ReportDto {
    private String experimentUid;
    private String metricName;
    private String metricSource;
    private String categoryName;
    private String categoryValue;
    private boolean isAdditive;
    private StatisticsDataDto statisticsData;
    private Timestamp timestamp;

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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setTs(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryValue() {
        return categoryValue;
    }

    public void setCategoryValue(String categoryValue) {
        this.categoryValue = categoryValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportDto reportDto = (ReportDto) o;
        return isAdditive == reportDto.isAdditive
                && Objects.equals(experimentUid, reportDto.experimentUid)
                && Objects.equals(metricName, reportDto.metricName)
                && Objects.equals(metricSource, reportDto.metricSource)
                && Objects.equals(categoryName, reportDto.categoryName)
                && Objects.equals(categoryValue, reportDto.categoryValue)
                && Objects.equals(statisticsData, reportDto.statisticsData)
                && Objects.equals(timestamp, reportDto.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(experimentUid,
                metricName,
                metricSource,
                categoryName,
                categoryValue,
                isAdditive,
                statisticsData,
                timestamp);
    }

    @Override
    public String toString() {
        return "ReportDto{" +
                "experimentUid='" + experimentUid + '\'' +
                ", metricName='" + metricName + '\'' +
                ", metricSource='" + metricSource + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", categoryValue='" + categoryValue + '\'' +
                ", isAdditive=" + isAdditive +
                ", statisticsData=" + statisticsData +
                ", timestamp=" + timestamp +
                '}';
    }
}
