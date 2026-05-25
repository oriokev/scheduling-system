package com.oriokev.schedulingsystem.schema;

import com.oriokev.schedulingsystem.domain.TaskType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TaskSchemaRegistry {

    private static final Map<TaskType, List<ParameterSchema>> REGISTRY = Map.of(
            TaskType.LOG_TASK, List.of(
                    new ParameterSchema("message", "string",  true,  "The message to write to the log"),
                    new ParameterSchema("level",   "string",  false, "Log level: INFO, WARN or ERROR (default INFO)")
            ),
            TaskType.EMAIL_TASK, List.of(
                    new ParameterSchema("to",      "string",  true,  "Recipient email address"),
                    new ParameterSchema("subject", "string",  true,  "Email subject line"),
                    new ParameterSchema("body",    "string",  false, "Email body text")
            ),
            TaskType.HTTP_REQUEST_TASK, List.of(
                    new ParameterSchema("url",    "string",  true,  "Target URL"),
                    new ParameterSchema("method", "string",  false, "HTTP method: GET, POST, PUT, DELETE (default GET)"),
                    new ParameterSchema("body",   "string",  false, "Request body (optional)")
            )
    );

    public List<ParameterSchema> getSchema(TaskType taskType) {
        return REGISTRY.getOrDefault(taskType, List.of());
    }

    public Map<TaskType, List<ParameterSchema>> getAll() {
        return REGISTRY;
    }

    public void validateParams(TaskType taskType, Map<String, String> params) {
        List<ParameterSchema> schema = getSchema(taskType);
        for (ParameterSchema field : schema) {
            if (field.required()) {
                String value = params == null ? null : params.get(field.name());
                if (value == null || value.isBlank()) {
                    throw new IllegalArgumentException(
                            "Required parameter '%s' is missing for task %s".formatted(field.name(), taskType));
                }
            }
        }
    }
}
