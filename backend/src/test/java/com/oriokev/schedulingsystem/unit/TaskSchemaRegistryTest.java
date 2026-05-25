package com.oriokev.schedulingsystem.unit;

import com.oriokev.schedulingsystem.domain.TaskType;
import com.oriokev.schedulingsystem.schema.ParameterSchema;
import com.oriokev.schedulingsystem.schema.TaskSchemaRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class TaskSchemaRegistryTest {

    private TaskSchemaRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new TaskSchemaRegistry();
    }

    @Test
    void allTaskTypes_haveSchemas() {
        for (TaskType type : TaskType.values()) {
            assertThat(registry.getSchema(type))
                    .as("Schema for %s should not be empty", type)
                    .isNotEmpty();
        }
    }

    @Test
    void logTask_hasRequiredMessageField() {
        List<ParameterSchema> schema = registry.getSchema(TaskType.LOG_TASK);

        ParameterSchema message = schema.stream()
                .filter(p -> p.name().equals("message"))
                .findFirst()
                .orElseThrow();

        assertThat(message.required()).isTrue();
        assertThat(message.type()).isEqualTo("string");
    }

    @Test
    void emailTask_toAndSubjectAreRequired() {
        List<ParameterSchema> schema = registry.getSchema(TaskType.EMAIL_TASK);

        assertThat(schema.stream().filter(p -> p.name().equals("to")).findFirst().orElseThrow().required()).isTrue();
        assertThat(schema.stream().filter(p -> p.name().equals("subject")).findFirst().orElseThrow().required()).isTrue();
        assertThat(schema.stream().filter(p -> p.name().equals("body")).findFirst().orElseThrow().required()).isFalse();
    }

    @Test
    void validateParams_missingRequired_throwsException() {
        assertThatThrownBy(() -> registry.validateParams(TaskType.LOG_TASK, Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("message");
    }

    @Test
    void validateParams_allRequiredPresent_passes() {
        assertThatCode(() -> registry.validateParams(TaskType.LOG_TASK, Map.of("message", "hello")))
                .doesNotThrowAnyException();
    }
}
