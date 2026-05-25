package com.oriokev.schedulingsystem.domain.schedule;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.oriokev.schedulingsystem.validation.ValidCron;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("CRON")
public record CronConfig(
        @NotBlank @ValidCron String expression
) implements ScheduleConfig {
}
