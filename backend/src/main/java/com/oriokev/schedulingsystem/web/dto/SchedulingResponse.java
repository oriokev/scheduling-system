package com.oriokev.schedulingsystem.web.dto;

import com.oriokev.schedulingsystem.domain.Scheduling;
import com.oriokev.schedulingsystem.domain.ScheduleStatus;
import com.oriokev.schedulingsystem.domain.ScheduleType;
import com.oriokev.schedulingsystem.domain.TaskType;
import com.oriokev.schedulingsystem.domain.schedule.ScheduleConfig;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record SchedulingResponse(
        UUID id,
        String name,
        String description,
        TaskType taskType,
        String taskTypeDisplayName,
        Map<String, String> taskParams,
        ScheduleType scheduleType,
        ScheduleConfig scheduleConfig,
        ScheduleStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastRunAt,
        LocalDateTime nextRunAt
) {
    public static SchedulingResponse from(Scheduling s) {
        return new SchedulingResponse(
                s.getId(),
                s.getName(),
                s.getDescription(),
                s.getTaskType(),
                s.getTaskType().getDisplayName(),
                s.getTaskParams(),
                s.getScheduleType(),
                s.getScheduleConfig(),
                s.getStatus(),
                s.getCreatedAt(),
                s.getUpdatedAt(),
                s.getLastRunAt(),
                s.getNextRunAt()
        );
    }
}
