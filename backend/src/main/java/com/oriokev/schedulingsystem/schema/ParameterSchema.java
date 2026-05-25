package com.oriokev.schedulingsystem.schema;

public record ParameterSchema(
        String name,
        String type,
        boolean required,
        String description
) {
}
