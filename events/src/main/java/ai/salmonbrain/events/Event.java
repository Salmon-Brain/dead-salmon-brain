package ai.salmonbrain.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

public class Event {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String uuid = UUID.randomUUID().toString();
    private final long timestamp = System.currentTimeMillis();

    public final String getUUID() {
        return uuid;
    }

    public final long getTimestamp() {
        return timestamp;
    }

    public final String toJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(this);
    }
}
