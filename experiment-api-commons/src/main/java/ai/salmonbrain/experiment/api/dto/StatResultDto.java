package ai.salmonbrain.experiment.api.dto;

import java.util.Objects;
public class StatResultDto {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatResultDto that = (StatResultDto) o;
        return Double.compare(that.statistic, statistic) == 0 && Double.compare(that.pValue, pValue) == 0 && Double.compare(that.requiredSampleSizeByVariant, requiredSampleSizeByVariant) == 0 && Double.compare(that.controlCentralTendency, controlCentralTendency) == 0 && Double.compare(that.treatmentCentralTendency, treatmentCentralTendency) == 0 && Double.compare(that.controlVariance, controlVariance) == 0 && Double.compare(that.treatmentVariance, treatmentVariance) == 0 && Double.compare(that.percentageLeft, percentageLeft) == 0 && Double.compare(that.percentageRight, percentageRight) == 0 && Objects.equals(centralTendencyType, that.centralTendencyType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statistic, pValue, requiredSampleSizeByVariant, controlCentralTendency, treatmentCentralTendency, controlVariance, treatmentVariance, percentageLeft, percentageRight, centralTendencyType);
    }

    @Override
    public String toString() {
        return "StatResultDto{" +
                "statistic=" + statistic +
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
