package com.oriokev.schedulingsystem.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oriokev.schedulingsystem.domain.schedule.ScheduleConfig;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ScheduleConfigConverter implements AttributeConverter<ScheduleConfig, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String convertToDatabaseColumn(ScheduleConfig config) {
        if (config == null) return null;
        try {
            return MAPPER.writeValueAsString(config);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot serialize ScheduleConfig", e);
        }
    }

    @Override
    public ScheduleConfig convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return MAPPER.readValue(json, ScheduleConfig.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot deserialize ScheduleConfig: " + json, e);
        }
    }
}
