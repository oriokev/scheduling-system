package com.oriokev.schedulingsystem.web;

import com.oriokev.schedulingsystem.domain.TaskType;
import com.oriokev.schedulingsystem.schema.ParameterSchema;
import com.oriokev.schedulingsystem.schema.TaskSchemaRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/task-types")
public class TaskTypeController {

    record TaskTypeDto(String value, String displayName, List<ParameterSchema> schema) {}

    private final TaskSchemaRegistry registry;

    public TaskTypeController(TaskSchemaRegistry registry) {
        this.registry = registry;
    }

    @GetMapping
    public List<TaskTypeDto> listTaskTypes() {
        return Arrays.stream(TaskType.values())
                .map(t -> new TaskTypeDto(t.name(), t.getDisplayName(), registry.getSchema(t)))
                .toList();
    }

    @GetMapping("/{taskType}/schema")
    public List<ParameterSchema> getSchema(@PathVariable TaskType taskType) {
        return registry.getSchema(taskType);
    }
}
