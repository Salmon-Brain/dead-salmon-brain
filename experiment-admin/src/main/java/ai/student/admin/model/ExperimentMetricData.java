package ai.student.admin.model;

import ai.student.admin.model.converters.StatisticsDataJsonConverter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class ExperimentMetricData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @ManyToOne
    private Experiment experiment;
    private String metricName;
    private Timestamp ts;
    @Column(columnDefinition = "jsonb")
    @Convert(converter = StatisticsDataJsonConverter.class)
    private StatisticsData statisticsData;

    public ExperimentMetricData() {
    }

    public ExperimentMetricData(String metricName,
                                Timestamp ts,
                                StatisticsData statisticsData) {
        this.metricName = metricName;
        this.ts = ts;
        this.statisticsData = statisticsData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

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

    public StatisticsData getStatisticsData() {
        return statisticsData;
    }

    public void setStatisticsData(StatisticsData statisticsData) {
        this.statisticsData = statisticsData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExperimentMetricData that = (ExperimentMetricData) o;
        return Objects.equals(id, that.id) && Objects.equals(experiment, that.experiment) && Objects.equals(metricName, that.metricName) && Objects.equals(ts, that.ts) && Objects.equals(statisticsData, that.statisticsData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, experiment, metricName, ts, statisticsData);
    }

    @Override
    public String toString() {
        return "ExperimentMetricData{" +
                "id=" + id +
                ", experiment=" + experiment +
                ", metricName='" + metricName + '\'' +
                ", ts=" + ts +
                ", statisticsData=" + statisticsData +
                '}';
    }
}
