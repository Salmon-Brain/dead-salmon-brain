package ai.salmonbrain.admin.dto;


import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class ExperimentDto {
    private Long id;
    private String expUid;
    private Timestamp timestamp;
    private List<ExperimentMetricDataDto> metricData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExpUid() {
        return expUid;
    }

    public void setExpUid(String expUid) {
        this.expUid = expUid;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public List<ExperimentMetricDataDto> getMetricData() {
        return metricData;
    }

    public void setMetricData(List<ExperimentMetricDataDto> metricData) {
        this.metricData = metricData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExperimentDto that = (ExperimentDto) o;
        return Objects.equals(id, that.id) && Objects.equals(expUid, that.expUid) && Objects.equals(timestamp, that.timestamp) && Objects.equals(metricData, that.metricData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, expUid, timestamp, metricData);
    }

    @Override
    public String toString() {
        return "ExperimentDto{" +
                "id=" + id +
                ", expUid='" + expUid + '\'' +
                ", timestamp=" + timestamp +
                ", metricData=" + metricData +
                '}';
    }
}
