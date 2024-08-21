package com.chrzanowy.graphanalysis.db.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(Map<String, Object> customerInfo) {
        try {
            return objectMapper.writeValueAsString(customerInfo);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }
        return "{}";
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String customerInfoJSON) {
        try {
            return objectMapper.readValue(customerInfoJSON,
                new TypeReference<>() {
                });
        } catch (final IOException e) {
            log.error("JSON reading error", e);
        }
        return Map.of();
    }
}