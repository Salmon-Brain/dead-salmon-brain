package ai.salmonbrain.admin.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Experiment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    private String expUid;
    private Timestamp timestamp;
    @OneToMany(mappedBy = "experiment", fetch = FetchType.LAZY)
    private List<ExperimentMetricData> metricData;

    public Experiment() {
    }

    public Experiment(String expUid, Timestamp timestamp) {
        this.expUid = expUid;
        this.timestamp = timestamp;
    }

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

    public List<ExperimentMetricData> getMetricData() {
        return metricData;
    }

    public void setMetricData(List<ExperimentMetricData> metricData) {
        this.metricData = metricData;
    }

    public void append(ExperimentMetricData data) {
        if(this.metricData == null) {
            metricData = new ArrayList<>();
        }
        metricData.add(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Experiment that = (Experiment) o;
        return Objects.equals(id, that.id) && Objects.equals(expUid, that.expUid) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, expUid, timestamp);
    }

    @Override
    public String toString() {
        return "Experiment{" +
                "id=" + id +
                ", expUid='" + expUid + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
