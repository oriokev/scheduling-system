package com.oriokev.schedulingsystem.schema;

import java.util.List;

public record ParameterSchema(
        String name,
        String type,
        boolean required,
        String description,
        List<String> options,
        String defaultValue
) {
}
