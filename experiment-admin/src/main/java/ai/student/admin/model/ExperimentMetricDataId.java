package ai.student.admin.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

public class ExperimentMetricDataId implements Serializable {
    private String expUid;
    private String metricName;
    private Timestamp ts;


    public ExperimentMetricDataId() {
    }

    public ExperimentMetricDataId(String expUid, String metricName, Timestamp ts) {
        this.expUid = expUid;
        this.metricName = metricName;
        this.ts = ts;
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

    public void setExpUid(String expUid) {
        this.expUid = expUid;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public void setTs(Timestamp ts) {
        this.ts = ts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExperimentMetricDataId that = (ExperimentMetricDataId) o;
        return Objects.equals(expUid, that.expUid) && Objects.equals(metricName, that.metricName) && Objects.equals(ts, that.ts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expUid, metricName, ts);
    }

    @Override
    public String toString() {
        return "ExperimentMetricDataId{" +
                "expUid='" + expUid + '\'' +
                ", metricName='" + metricName + '\'' +
                ", ts=" + ts +
                '}';
    }
}
