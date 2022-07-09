package ai.salmonbrain.events;

public class FunnelEvent extends ExperimentEvent {
    public static final EventType TYPE = EventType.FUNNEL;
    private short step;
    private String stepName;
    private String funnelName;
    private Double value;

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

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FunnelEvent{" +
                "step=" + step +
                ", stepName='" + stepName + '\'' +
                ", funnelName='" + funnelName + '\'' +
                ", value=" + value +
                '}';
    }
}
