package ai.salmonbrain.events;


import java.util.Objects;

public class ErrorEvent extends ExperimentEvent {
    public final EventType TYPE = EventType.ERROR;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ErrorEvent)) return false;
        if (!super.equals(o)) return false;
        ErrorEvent that = (ErrorEvent) o;
        return TYPE == that.TYPE && getSeverity() == that.getSeverity() && Objects.equals(getMessage(), that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), TYPE, getSeverity(), getMessage());
    }

    @Override
    public String toString() {
        return "ErrorEvent{" +
                "TYPE=" + TYPE +
                ", severity=" + severity +
                ", message='" + message + '\'' +
                "} " + super.toString();
    }
}
