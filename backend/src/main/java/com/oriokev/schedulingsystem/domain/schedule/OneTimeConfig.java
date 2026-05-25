package com.oriokev.schedulingsystem.domain.schedule;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@JsonTypeName("ONE_TIME")
public record OneTimeConfig(
        @NotNull LocalDateTime runAt
) implements ScheduleConfig {
}
