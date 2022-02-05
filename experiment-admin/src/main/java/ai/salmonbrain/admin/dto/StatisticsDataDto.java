package ai.salmonbrain.admin.dto;

import java.util.Objects;

public class StatisticsDataDto {
    private StatResultDto statResult;
    private boolean srm;
    private long controlSize;
    private long treatmentSize;
    private String testType;

    private double alpha;
    private double beta;

    private boolean isAdditive;
    private String metricSource;

    private String categoryName;
    private String categoryValue;


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

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
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
        StatisticsDataDto that = (StatisticsDataDto) o;
        return srm == that.srm
                && controlSize == that.controlSize
                && treatmentSize == that.treatmentSize
                && Double.compare(that.alpha, alpha) == 0
                && Double.compare(that.beta, beta) == 0
                && isAdditive == that.isAdditive
                && Objects.equals(statResult, that.statResult)
                && Objects.equals(testType, that.testType)
                && Objects.equals(metricSource, that.metricSource)
                && Objects.equals(categoryName, that.categoryName)
                && Objects.equals(categoryValue, that.categoryValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statResult, srm, controlSize, treatmentSize, testType, alpha, beta, isAdditive, metricSource, categoryName, categoryValue);
    }

    @Override
    public String toString() {
        return "StatisticsDataDto{" +
                "statResult=" + statResult +
                ", srm=" + srm +
                ", controlSize=" + controlSize +
                ", treatmentSize=" + treatmentSize +
                ", testType='" + testType + '\'' +
                ", alpha=" + alpha +
                ", beta=" + beta +
                ", isAdditive=" + isAdditive +
                ", metricSource='" + metricSource + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", categoryValue='" + categoryValue + '\'' +
                '}';
    }
}
