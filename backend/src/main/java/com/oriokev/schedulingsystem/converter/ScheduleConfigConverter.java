package com.oriokev.schedulingsystem.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oriokev.schedulingsystem.domain.schedule.ScheduleConfig;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = true)
public class ScheduleConfigConverter implements AttributeConverter<ScheduleConfig, String> {

    private final ObjectMapper mapper;

    public ScheduleConfigConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String convertToDatabaseColumn(ScheduleConfig config) {
        if (config == null) return null;
        try {
            return mapper.writeValueAsString(config);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot serialize ScheduleConfig", e);
        }
    }

    @Override
    public ScheduleConfig convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return mapper.readValue(json, ScheduleConfig.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot deserialize ScheduleConfig: " + json, e);
        }
    }
}
