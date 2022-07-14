package ai.salmonbrain.events;

import java.util.Objects;

public class BehavioralEvent extends ExperimentEvent {
    public final EventType TYPE = EventType.BEHAVIOUR;
    private String eventId;
    private double value;
    public BehavioralEvent(){}

    public BehavioralEvent(
            String entityUid,
            String categoryName,
            String categoryValue,
            String variantId,
            String experimentUid,
            String eventId,
            Double value
    ) {
        super(entityUid, categoryName, categoryValue, variantId, experimentUid);
        this.eventId = eventId;
        this.value = value;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BehavioralEvent)) return false;
        if (!super.equals(o)) return false;
        BehavioralEvent that = (BehavioralEvent) o;
        return Double.compare(that.getValue(), getValue()) == 0 && TYPE == that.TYPE && Objects.equals(getEventId(), that.getEventId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), TYPE, getEventId(), getValue());
    }

    @Override
    public String toString() {
        return "BehavioralEvent{" +
                "TYPE=" + TYPE +
                ", eventId='" + eventId + '\'' +
                ", value=" + value +
                "} " + super.toString();
    }
}
