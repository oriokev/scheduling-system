package com.oriokev.schedulingsystem.schema;

import com.oriokev.schedulingsystem.domain.TaskType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TaskSchemaRegistry {

    private static final Map<TaskType, List<ParameterSchema>> REGISTRY = Map.of(
            TaskType.LOG_TASK, List.of(
                    new ParameterSchema("message", "string", true,  "The message to write to the log", null,  null),
                    new ParameterSchema("level",   "string", false, "Log level",
                            List.of("INFO", "WARN", "ERROR"), "INFO")
            ),
            TaskType.EMAIL_TASK, List.of(
                    new ParameterSchema("to",      "string", true,  "Recipient email address", null, null),
                    new ParameterSchema("subject", "string", true,  "Email subject line",       null, null),
                    new ParameterSchema("body",    "string", false, "Email body text",           null, null)
            ),
            TaskType.HTTP_REQUEST_TASK, List.of(
                    new ParameterSchema("url",    "string", true,  "Target URL",               null, null),
                    new ParameterSchema("method", "string", false, "HTTP method",
                            List.of("GET", "POST", "PUT", "DELETE", "PATCH"), "GET"),
                    new ParameterSchema("body",   "string", false, "Request body (optional)",  null, null)
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
