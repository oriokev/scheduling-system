package com.oriokev.schedulingsystem.task;

import com.oriokev.schedulingsystem.domain.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HttpRequestTaskExecutor implements TaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestTaskExecutor.class);

    @Override
    public TaskType getTaskType() {
        return TaskType.HTTP_REQUEST_TASK;
    }

    @Override
    public void execute(Map<String, String> params) {
        String url    = params.getOrDefault("url", "(no url)");
        String method = params.getOrDefault("method", "GET").toUpperCase();
        String body   = params.getOrDefault("body", "");

        log.info("[HTTP REQUEST TASK] {} {} body=\"{}\"", method, url, body);
    }
}
