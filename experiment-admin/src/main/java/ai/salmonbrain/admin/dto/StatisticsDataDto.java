package ai.salmonbrain.admin.dto;

import java.util.Objects;

public class StatisticsDataDto {
    private StatResultDto statResult;
    private boolean srm;
    private long controlSize;
    private long treatmentSize;
    private String testType;

    private boolean isAdditive;
    private String metricSource;

    public StatResultDto getStatResult() {
        return statResult;
    }

    public void setStatResult(StatResultDto statResult) {
        this.statResult = statResult;
    }

    public boolean isSrm() {
        return srm;
    }

    public void setSrm(boolean srm) {
        this.srm = srm;
    }

    public long getControlSize() {
        return controlSize;
    }

    public void setControlSize(long controlSize) {
        this.controlSize = controlSize;
    }

    public long getTreatmentSize() {
        return treatmentSize;
    }

    public void setTreatmentSize(long treatmentSize) {
        this.treatmentSize = treatmentSize;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public boolean isAdditive() {
        return isAdditive;
    }

    public void setAdditive(boolean additive) {
        isAdditive = additive;
    }

    public String getMetricSource() {
        return metricSource;
    }

    public void setMetricSource(String metricSource) {
        this.metricSource = metricSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticsDataDto that = (StatisticsDataDto) o;
        return srm == that.srm && controlSize == that.controlSize && treatmentSize == that.treatmentSize && isAdditive == that.isAdditive && Objects.equals(statResult, that.statResult) && Objects.equals(testType, that.testType) && Objects.equals(metricSource, that.metricSource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statResult, srm, controlSize, treatmentSize, testType, isAdditive, metricSource);
    }

    @Override
    public String toString() {
        return "StatisticsDataDto{" +
                "statResult=" + statResult +
                ", srm=" + srm +
                ", controlSize=" + controlSize +
                ", treatmentSize=" + treatmentSize +
                ", testType='" + testType + '\'' +
                ", isAdditive=" + isAdditive +
                ", metricSource='" + metricSource + '\'' +
                '}';
    }
}
