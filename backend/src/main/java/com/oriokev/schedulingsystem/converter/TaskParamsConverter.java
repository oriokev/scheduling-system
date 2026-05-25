package com.oriokev.schedulingsystem.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.HashMap;
import java.util.Map;

@Converter
public class TaskParamsConverter implements AttributeConverter<Map<String, String>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, String>> TYPE_REF = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(Map<String, String> params) {
        if (params == null) return "{}";
        try {
            return MAPPER.writeValueAsString(params);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot serialize task params", e);
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) return new HashMap<>();
        try {
            return MAPPER.readValue(json, TYPE_REF);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot deserialize task params: " + json, e);
        }
    }
}
