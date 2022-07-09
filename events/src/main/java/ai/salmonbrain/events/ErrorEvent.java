package ai.salmonbrain.events;


public class ErrorEvent extends ExperimentEvent {
    public static final EventType TYPE = EventType.ERROR;
    private Severity severity;
    private String message;

    ErrorEvent() {
    }

    public ErrorEvent(
            String entityUid,
            String categoryName,
            String categoryValue,
            String variantId,
            String experimentUid,
            Severity severity,
            String message
    ) {
        super(entityUid, categoryName, categoryValue, variantId, experimentUid);
        this.severity = severity;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    @Override
    public String toString() {
        return "ErrorEvent{" +
                "severity=" + severity +
                ", message='" + message + '\'' +
                '}';
    }
}
