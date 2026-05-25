package com.oriokev.schedulingsystem.task;

import com.oriokev.schedulingsystem.domain.TaskType;

import java.util.Map;

public interface TaskExecutor {

    TaskType getTaskType();

    void execute(Map<String, String> params);
}
