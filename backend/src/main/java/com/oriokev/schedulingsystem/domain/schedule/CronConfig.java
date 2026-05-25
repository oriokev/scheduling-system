package com.oriokev.schedulingsystem.domain.schedule;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("CRON")
public record CronConfig(
        @NotBlank String expression
) implements ScheduleConfig {
}
