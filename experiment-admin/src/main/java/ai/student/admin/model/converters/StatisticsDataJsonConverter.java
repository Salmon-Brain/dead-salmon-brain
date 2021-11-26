package ai.student.admin.model.converters;

import ai.student.admin.model.StatisticsData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.UncheckedIOException;

@Converter(autoApply = true)
public class StatisticsDataJsonConverter implements AttributeConverter<StatisticsData, String> {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(StatisticsData attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public StatisticsData convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, StatisticsData.class);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
