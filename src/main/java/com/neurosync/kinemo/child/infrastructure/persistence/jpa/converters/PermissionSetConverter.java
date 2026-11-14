package com.neurosync.kinemo.child.infrastructure.persistence.jpa.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neurosync.kinemo.child.domain.model.valueobjects.PermissionSet;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;
import java.util.Set;

@Converter(autoApply = false)
public class PermissionSetConverter implements AttributeConverter<PermissionSet, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> PERMISSIONS_TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(PermissionSet permissionSet) {
        if (permissionSet == null) {
            return null;
        }

        try {
            List<String> permissions = permissionSet.permissions().stream()
                    .sorted()
                    .toList();
            return OBJECT_MAPPER.writeValueAsString(permissions);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Could not serialize permissions", exception);
        }
    }

    @Override
    public PermissionSet convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }

        try {
            List<String> permissions = OBJECT_MAPPER.readValue(value, PERMISSIONS_TYPE);
            return permissions == null ? null : new PermissionSet(Set.copyOf(permissions));
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Could not deserialize permissions", exception);
        }
    }
}
