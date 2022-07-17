package ai.salmonbrain.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class EventTest {
    private static final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    @Test
    public void serdeBehavioralEventTest() throws JsonProcessingException {
        String eventData = "{\"uuid\":\"711e0a7f-1724-453a-8147-2933a13b8595\",\"timestamp\":1648335545907,\"entityUid\":\"1\",\"categoryName\":\"common\",\"categoryValue\":\"all\",\"variantId\":\"treatment\",\"experimentUid\":\"testA\",\"TYPE\":\"BEHAVIOUR\",\"eventId\":\"MainPage:BigRedButton:Show\",\"value\":0.4}";
        BehavioralEvent event = objectMapper.readValue(eventData, BehavioralEvent.class);
        assertEquals(objectMapper.writeValueAsString(event), eventData);

    }
    @Test
    public void serdeAdEventTest() throws JsonProcessingException {
        String adData = "{\"uuid\":\"a664e1cc-12c1-46e0-b68a-8358d6b41e06\",\"timestamp\":1657817778583,\"entityUid\":\"711e0a7f-1724-453a-8147-2933a13b8595\",\"categoryName\":\"common\",\"categoryValue\":\"all\",\"variantId\":\"treatment\",\"experimentUid\":\"testA\",\"TYPE\":\"AD\",\"sdkName\":\"android\",\"placement\":\"main\",\"adType\":\"self\",\"action\":\"click\",\"duration\":2,\"first\":false}";
        AdEvent event = objectMapper.readValue(adData, AdEvent.class);
        assertEquals(objectMapper.writeValueAsString(event), adData);
    }

    @Test
    public void serdeErrorEventTest() throws JsonProcessingException {
        String errorData = "{\"uuid\":\"a664e1cc-12c1-46e0-b68a-8358d6b41e06\",\"timestamp\":1657817778583,\"entityUid\":\"711e0a7f-1724-453a-8147-2933a13b8595\",\"categoryName\":\"common\",\"categoryValue\":\"all\",\"variantId\":\"treatment\",\"experimentUid\":\"testA\",\"TYPE\":\"ERROR\",\"severity\":\"INFO\",\"message\":\"rock\"}";
        ErrorEvent event = objectMapper.readValue(errorData, ErrorEvent.class);
        assertEquals(objectMapper.writeValueAsString(event), errorData);
    }

    @Test
    public void serdeFunnelEventTest() throws JsonProcessingException {
        String funnelData = "{\"uuid\":\"a664e1cc-12c1-46e0-b68a-8358d6b41e06\",\"timestamp\":1657817778583,\"entityUid\":\"711e0a7f-1724-453a-8147-2933a13b8595\",\"categoryName\":\"common\",\"categoryValue\":\"all\",\"variantId\":\"treatment\",\"experimentUid\":\"testA\",\"TYPE\":\"FUNNEL\",\"step\":0,\"stepName\":\"add\",\"funnelName\":\"buy\",\"value\":0.0}";
        FunnelEvent event = objectMapper.readValue(funnelData, FunnelEvent.class);
        assertEquals(objectMapper.writeValueAsString(event), funnelData);
    }

    @Test
    public void serdePurchaseEventTest() throws JsonProcessingException {
        String funnelData = "{\"uuid\":\"a664e1cc-12c1-46e0-b68a-8358d6b41e06\",\"timestamp\":1657817778583,\"entityUid\":\"711e0a7f-1724-453a-8147-2933a13b8595\",\"categoryName\":\"common\",\"categoryValue\":\"all\",\"variantId\":\"treatment\",\"experimentUid\":\"testA\",\"TYPE\":\"PURCHASE\",\"place\":\"game\",\"itemType\":\"sword\",\"itemId\":\"sword12\",\"amount\":1,\"currency\":\"usd\"}";
        PurchaseEvent event = objectMapper.readValue(funnelData, PurchaseEvent.class);
        assertEquals(objectMapper.writeValueAsString(event), funnelData);
    }
}