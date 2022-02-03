package ai.salmonbrain.admin.model;

import java.util.Objects;

public class StatisticsData {

    private StatResult statResult;
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

    public StatisticsData() {
    }

    public StatisticsData(StatResult statResult,
                          boolean srm,
                          long controlSize,
                          long treatmentSize,
                          double alpha,
                          double beta,
                          String testType,
                          String metricSource,
                          String categoryName,
                          String categoryValue,
                          boolean isAdditive) {
        this.statResult = statResult;
        this.srm = srm;
        this.controlSize = controlSize;
        this.treatmentSize = treatmentSize;
        this.alpha = alpha;
        this.beta = beta;
        this.testType = testType;
        this.metricSource = metricSource;
        this.isAdditive = isAdditive;
        this.categoryName = categoryName;
        this.categoryValue = categoryValue;
    }

    public StatResult getStatResult() {
        return statResult;
    }

    public void setStatResult(StatResult statResult) {
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
        StatisticsData data = (StatisticsData) o;
        return srm == data.srm
                && controlSize == data.controlSize
                && treatmentSize == data.treatmentSize
                && Double.compare(data.alpha, alpha) == 0
                && Double.compare(data.beta, beta) == 0
                && isAdditive == data.isAdditive
                && Objects.equals(statResult, data.statResult)
                && Objects.equals(testType, data.testType)
                && Objects.equals(metricSource, data.metricSource)
                && Objects.equals(categoryName, data.categoryName)
                && Objects.equals(categoryValue, data.categoryValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statResult, srm, controlSize, treatmentSize, testType, alpha, beta, isAdditive, metricSource, categoryName, categoryValue);
    }

    @Override
    public String toString() {
        return "StatisticsData{" +
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
