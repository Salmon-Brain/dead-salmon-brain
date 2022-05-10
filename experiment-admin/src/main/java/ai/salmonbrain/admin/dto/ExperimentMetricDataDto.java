package ai.salmonbrain.admin.dto;

import java.sql.Timestamp;
import java.util.Objects;

public class ExperimentMetricDataDto {
    private String metricName;
    private Timestamp timestamp;
    private StatisticsDataDto statisticsData;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public StatisticsDataDto getStatisticsData() {
        return statisticsData;
    }

    public void setStatisticsData(StatisticsDataDto statisticsData) {
        this.statisticsData = statisticsData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExperimentMetricDataDto that = (ExperimentMetricDataDto) o;
        return Objects.equals(metricName, that.metricName) && Objects.equals(timestamp, that.timestamp) && Objects.equals(statisticsData, that.statisticsData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metricName, timestamp, statisticsData);
    }

    @Override
    public String toString() {
        return "ExperimentMetricDataDto{" +
                "metricName='" + metricName + '\'' +
                ", timestamp=" + timestamp +
                ", statisticsData=" + statisticsData +
                '}';
    }
}
