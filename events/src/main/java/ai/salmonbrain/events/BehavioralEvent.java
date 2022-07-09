package ai.salmonbrain.events;


public class BehavioralEvent extends ExperimentEvent {
    public static final EventType TYPE = EventType.BEHAVIOUR;
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
    public String toString() {
        return "UiEvent{" +
                "eventId='" + eventId + '\'' +
                ", value=" + value +
                '}';
    }
}
