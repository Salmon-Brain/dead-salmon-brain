package ai.salmonbrain.events;

import java.util.Objects;
import java.util.UUID;

public abstract class Event {
    private final String uuid = UUID.randomUUID().toString();
    private final long timestamp = System.currentTimeMillis();

    public final String getUUID() {
        return uuid;
    }

    public final long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return getTimestamp() == event.getTimestamp() && Objects.equals(uuid, event.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, getTimestamp());
    }

    @Override
    public String toString() {
        return "Event{" +
                "uuid='" + uuid + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
