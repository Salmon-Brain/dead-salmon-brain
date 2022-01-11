package ai.student.admin.dto;

import java.sql.Timestamp;
import java.util.Objects;

public class ExperimentMetricDataDto {
    private String metricName;
    private Timestamp ts;
    private StatisticsDataDto statisticsData;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Timestamp getTs() {
        return ts;
    }

    public void setTs(Timestamp ts) {
        this.ts = ts;
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
        return Objects.equals(metricName, that.metricName) && Objects.equals(ts, that.ts) && Objects.equals(statisticsData, that.statisticsData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metricName, ts, statisticsData);
    }

    @Override
    public String toString() {
        return "ExperimentMetricDataDto{" +
                "metricName='" + metricName + '\'' +
                ", ts=" + ts +
                ", statisticsData=" + statisticsData +
                '}';
    }
}
