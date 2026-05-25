package com.oriokev.schedulingsystem.domain.schedule;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.oriokev.schedulingsystem.domain.IntervalUnit;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@JsonTypeName("RECURRING")
public record RecurringConfig(
        @Min(1) int intervalValue,
        @NotNull IntervalUnit intervalUnit
) implements ScheduleConfig {
}
