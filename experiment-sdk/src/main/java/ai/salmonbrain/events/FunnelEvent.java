package ai.salmonbrain.events;

import java.util.Objects;

public class FunnelEvent extends ExperimentEvent {
    public final EventType TYPE = EventType.FUNNEL;
    private short step;
    private String stepName;
    private String funnelName;
    private double value;

    public FunnelEvent(){}

    public FunnelEvent(
            String entityUid,
            String categoryName,
            String categoryValue,
            String variantId,
            String experimentUid,
            short step,
            String stepName,
            String funnelName,
            Double value
    ) {
        super(entityUid, categoryName, categoryValue, variantId, experimentUid);
        this.step = step;
        this.stepName = stepName;
        this.funnelName = funnelName;
        this.value = value;
    }

    public short getStep() {
        return step;
    }

    public void setStep(short step) {
        this.step = step;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getFunnelName() {
        return funnelName;
    }

    public void setFunnelName(String funnelName) {
        this.funnelName = funnelName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FunnelEvent{" +
                "TYPE=" + TYPE +
                ", step=" + step +
                ", stepName='" + stepName + '\'' +
                ", funnelName='" + funnelName + '\'' +
                ", value=" + value +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunnelEvent)) return false;
        if (!super.equals(o)) return false;
        FunnelEvent that = (FunnelEvent) o;
        return getStep() == that.getStep() && TYPE == that.TYPE && Objects.equals(getStepName(), that.getStepName()) && Objects.equals(getFunnelName(), that.getFunnelName()) && Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), TYPE, getStep(), getStepName(), getFunnelName(), getValue());
    }
}
