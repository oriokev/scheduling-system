package com.oriokev.schedulingsystem.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Converter(autoApply = true)
public class TaskParamsConverter implements AttributeConverter<Map<String, String>, String> {

    private static final TypeReference<Map<String, String>> TYPE_REF = new TypeReference<>() {};

    private final ObjectMapper mapper;

    public TaskParamsConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String convertToDatabaseColumn(Map<String, String> params) {
        if (params == null) return "{}";
        try {
            return mapper.writeValueAsString(params);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot serialize task params", e);
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) return new HashMap<>();
        try {
            return mapper.readValue(json, TYPE_REF);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot deserialize task params: " + json, e);
        }
    }
}
