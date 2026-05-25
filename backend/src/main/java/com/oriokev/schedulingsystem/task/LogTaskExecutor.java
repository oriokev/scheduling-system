package com.oriokev.schedulingsystem.task;

import com.oriokev.schedulingsystem.domain.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LogTaskExecutor implements TaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(LogTaskExecutor.class);

    @Override
    public TaskType getTaskType() {
        return TaskType.LOG_TASK;
    }

    @Override
    public void execute(Map<String, String> params) {
        String message = params.getOrDefault("message", "(no message)");
        String level   = params.getOrDefault("level", "INFO").toUpperCase();

        switch (level) {
            case "WARN"  -> log.warn("[LOG TASK] {}", message);
            case "ERROR" -> log.error("[LOG TASK] {}", message);
            default      -> log.info("[LOG TASK] {}", message);
        }
    }
}
