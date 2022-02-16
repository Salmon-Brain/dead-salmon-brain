package ai.salmonbrain.admin.model;

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
    private String categoryName;
    private String categoryValue;

    private boolean srm;
    private long controlSize;
    private long treatmentSize;
    private String testType;
    private double alpha;
    private double beta;
    private boolean isAdditive;
    private String metricSource;

    private double statistic;
    private double pValue;
    private double requiredSampleSizeByVariant;
    private double controlCentralTendency;
    private double treatmentCentralTendency;
    private double controlVariance;
    private double treatmentVariance;
    private double percentageLeft;
    private double percentageRight;
    private String centralTendencyType;

    public ExperimentMetricData() {
    }

    public ExperimentMetricData(String metricName,
                                Timestamp ts,
                                String categoryName,
                                String categoryValue,
                                boolean srm,
                                long controlSize,
                                long treatmentSize,
                                String testType,
                                double alpha,
                                double beta,
                                boolean isAdditive,
                                String metricSource,
                                double statistic,
                                double pValue,
                                double requiredSampleSizeByVariant,
                                double controlCentralTendency,
                                double treatmentCentralTendency,
                                double controlVariance,
                                double treatmentVariance,
                                double percentageLeft,
                                double percentageRight,
                                String centralTendencyType) {
        this.metricName = metricName;
        this.ts = ts;
        this.categoryName = categoryName;
        this.categoryValue = categoryValue;
        this.srm = srm;
        this.controlSize = controlSize;
        this.treatmentSize = treatmentSize;
        this.testType = testType;
        this.alpha = alpha;
        this.beta = beta;
        this.isAdditive = isAdditive;
        this.metricSource = metricSource;
        this.statistic = statistic;
        this.pValue = pValue;
        this.requiredSampleSizeByVariant = requiredSampleSizeByVariant;
        this.controlCentralTendency = controlCentralTendency;
        this.treatmentCentralTendency = treatmentCentralTendency;
        this.controlVariance = controlVariance;
        this.treatmentVariance = treatmentVariance;
        this.percentageLeft = percentageLeft;
        this.percentageRight = percentageRight;
        this.centralTendencyType = centralTendencyType;
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

    public double getStatistic() {
        return statistic;
    }

    public void setStatistic(double statistic) {
        this.statistic = statistic;
    }

    public double getPValue() {
        return pValue;
    }

    public void setPValue(double pValue) {
        this.pValue = pValue;
    }

    public double getRequiredSampleSizeByVariant() {
        return requiredSampleSizeByVariant;
    }

    public void setRequiredSampleSizeByVariant(double requiredSampleSizeByVariant) {
        this.requiredSampleSizeByVariant = requiredSampleSizeByVariant;
    }

    public double getControlCentralTendency() {
        return controlCentralTendency;
    }

    public void setControlCentralTendency(double controlCentralTendency) {
        this.controlCentralTendency = controlCentralTendency;
    }

    public double getTreatmentCentralTendency() {
        return treatmentCentralTendency;
    }

    public void setTreatmentCentralTendency(double treatmentCentralTendency) {
        this.treatmentCentralTendency = treatmentCentralTendency;
    }

    public double getControlVariance() {
        return controlVariance;
    }

    public void setControlVariance(double controlVariance) {
        this.controlVariance = controlVariance;
    }

    public double getTreatmentVariance() {
        return treatmentVariance;
    }

    public void setTreatmentVariance(double treatmentVariance) {
        this.treatmentVariance = treatmentVariance;
    }

    public double getPercentageLeft() {
        return percentageLeft;
    }

    public void setPercentageLeft(double percentageLeft) {
        this.percentageLeft = percentageLeft;
    }

    public double getPercentageRight() {
        return percentageRight;
    }

    public void setPercentageRight(double percentageRight) {
        this.percentageRight = percentageRight;
    }

    public String getCentralTendencyType() {
        return centralTendencyType;
    }

    public void setCentralTendencyType(String centralTendencyType) {
        this.centralTendencyType = centralTendencyType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExperimentMetricData that = (ExperimentMetricData) o;
        return srm == that.srm
                && controlSize == that.controlSize
                && treatmentSize == that.treatmentSize
                && Double.compare(that.alpha, alpha) == 0
                && Double.compare(that.beta, beta) == 0
                && isAdditive == that.isAdditive
                && Double.compare(that.statistic, statistic) == 0
                && Double.compare(that.pValue, pValue) == 0
                && Double.compare(that.requiredSampleSizeByVariant, requiredSampleSizeByVariant) == 0
                && Double.compare(that.controlCentralTendency, controlCentralTendency) == 0
                && Double.compare(that.treatmentCentralTendency, treatmentCentralTendency) == 0
                && Double.compare(that.controlVariance, controlVariance) == 0
                && Double.compare(that.treatmentVariance, treatmentVariance) == 0
                && Double.compare(that.percentageLeft, percentageLeft) == 0
                && Double.compare(that.percentageRight, percentageRight) == 0
                && Objects.equals(id, that.id)
                && Objects.equals(experiment, that.experiment)
                && Objects.equals(metricName, that.metricName)
                && Objects.equals(ts, that.ts)
                && Objects.equals(testType, that.testType)
                && Objects.equals(metricSource, that.metricSource)
                && Objects.equals(categoryName, that.categoryName)
                && Objects.equals(categoryValue, that.categoryValue)
                && Objects.equals(centralTendencyType, that.centralTendencyType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, experiment, metricName, ts, srm, controlSize, treatmentSize, testType,
                alpha, beta, isAdditive, metricSource, categoryName, categoryValue, statistic, pValue,
                requiredSampleSizeByVariant, controlCentralTendency, treatmentCentralTendency, controlVariance,
                treatmentVariance, percentageLeft, percentageRight, centralTendencyType);
    }

    @Override
    public String toString() {
        return "ExperimentMetricData{" +
                "id=" + id +
                ", experiment=" + experiment +
                ", metricName='" + metricName + '\'' +
                ", ts=" + ts +
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
                ", statistic=" + statistic +
                ", pValue=" + pValue +
                ", requiredSampleSizeByVariant=" + requiredSampleSizeByVariant +
                ", controlCentralTendency=" + controlCentralTendency +
                ", treatmentCentralTendency=" + treatmentCentralTendency +
                ", controlVariance=" + controlVariance +
                ", treatmentVariance=" + treatmentVariance +
                ", percentageLeft=" + percentageLeft +
                ", percentageRight=" + percentageRight +
                ", centralTendencyType='" + centralTendencyType + '\'' +
                '}';
    }
}
