package ai.salmonbrain.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class BehavioralEventTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void serdeBehavioralEventTest() throws JsonProcessingException {
        String eventData = "{\"uuid\":\"711e0a7f-1724-453a-8147-2933a13b8595\",\"timestamp\":1648335545907,\"entityUid\":\"1\",\"categoryName\":\"common\",\"categoryValue\":\"all\",\"variantId\":\"treatment\",\"experimentUid\":\"testA\",\"eventId\":\"MainPage:BigRedButton:Show\",\"value\":0.4}";
        BehavioralEvent event = objectMapper.readValue(eventData, BehavioralEvent.class);
        assertEquals(eventData, event.toJson());

    }

    @Test
    public void serdeBehavioralEventTestWithoutExperimentalData() throws JsonProcessingException {
        String eventData = "{\"uuid\":\"711e0a7f-1724-453a-8147-2933a13b8595\",\"timestamp\":1648335545907,\"entityUid\":\"1\",\"eventId\":\"MainPage:BigRedButton:Show\",\"value\":0.4}";
        BehavioralEvent event = objectMapper.readValue(eventData, BehavioralEvent.class);
        assertEquals(eventData, event.toJson());

    }
}