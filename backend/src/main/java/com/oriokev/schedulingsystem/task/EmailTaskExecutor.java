package com.oriokev.schedulingsystem.task;

import com.oriokev.schedulingsystem.domain.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmailTaskExecutor implements TaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(EmailTaskExecutor.class);

    @Override
    public TaskType getTaskType() {
        return TaskType.EMAIL_TASK;
    }

    @Override
    public void execute(Map<String, String> params) {
        String to      = params.getOrDefault("to", "(unknown)");
        String subject = params.getOrDefault("subject", "(no subject)");
        String body    = params.getOrDefault("body", "");

        log.info("[EMAIL TASK] Sending email → to={}, subject=\"{}\", body=\"{}\"", to, subject, body);
    }
}
