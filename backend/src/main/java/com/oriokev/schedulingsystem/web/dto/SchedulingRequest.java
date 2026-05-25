package com.oriokev.schedulingsystem.web.dto;

import com.oriokev.schedulingsystem.domain.ScheduleType;
import com.oriokev.schedulingsystem.domain.TaskType;
import com.oriokev.schedulingsystem.domain.schedule.ScheduleConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record SchedulingRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 2000) String description,
        @NotNull TaskType taskType,
        @NotNull Map<String, String> taskParams,
        @NotNull ScheduleType scheduleType,
        @NotNull @Valid ScheduleConfig scheduleConfig
) {
}
