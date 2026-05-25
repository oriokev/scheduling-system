package com.oriokev.schedulingsystem.domain.schedule;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@JsonTypeName("WEEKLY")
public record WeeklyConfig(
        @NotBlank String dayOfWeek,
        @NotBlank @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$") String time
) implements ScheduleConfig {
}
