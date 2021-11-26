package ai.student.admin.model;

import ai.student.admin.model.converters.StatisticsDataJsonConverter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * {
 * "expUid": "exp1",
 * "metricName": "views",
 * "isAdditive": true,
 * "metricSource": "feedback",
 * "statisticsData": {
 * "statResult": {
 * "statistic": 17.89599532253776,
 * "pValue": 1.363646043214137e-70,
 * "requiredSampleSizeByVariant": 15,
 * "controlCentralTendency": 8.2108,
 * "treatmentCentralTendency": 4.1272,
 * "percentageLeft": -50.3037450332656,
 * "percentageRight": -49.16524702597345,
 * "centralTendencyType": "MEAN"
 * },
 * "srm": true,
 * "controlSize": 10000,
 * "treatmentSize": 20000,
 * "testType": "WELCH"
 * }
 * }
 */

@Entity
@IdClass(ExperimentMetricDataId.class)
public class ExperimentMetricData {
    @Id
    private String expUid;
    @Id
    private String metricName;
    @Id
    private Timestamp ts;
    @Column(columnDefinition = "jsonb")
    @Convert(converter = StatisticsDataJsonConverter.class)
    private StatisticsData statisticsData;

    public ExperimentMetricData(String expUid,
                                String metricName,
                                Timestamp ts,
                                StatisticsData statisticsData) {
        this.expUid = expUid;
        this.metricName = metricName;
        this.ts = ts;
        this.statisticsData = statisticsData;
    }

    public ExperimentMetricData() {
    }

    public String getExpUid() {
        return expUid;
    }

    public String getMetricName() {
        return metricName;
    }

    public Timestamp getTs() {
        return ts;
    }

    public StatisticsData getStatisticsData() {
        return statisticsData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExperimentMetricData that = (ExperimentMetricData) o;
        return Objects.equals(expUid, that.expUid) && Objects.equals(metricName, that.metricName) && Objects.equals(ts, that.ts) && Objects.equals(statisticsData, that.statisticsData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expUid, metricName, ts, statisticsData);
    }

    @Override
    public String toString() {
        return "ExperimentMetricData{" +
                "expUid='" + expUid + '\'' +
                ", metricName='" + metricName + '\'' +
                ", ts=" + ts +
                ", statisticsData=" + statisticsData +
                '}';
    }
}
