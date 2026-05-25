package com.oriokev.schedulingsystem.domain.schedule;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = OneTimeConfig.class,   name = "ONE_TIME"),
    @JsonSubTypes.Type(value = RecurringConfig.class, name = "RECURRING"),
    @JsonSubTypes.Type(value = WeeklyConfig.class,    name = "WEEKLY"),
    @JsonSubTypes.Type(value = CronConfig.class,      name = "CRON")
})
public sealed interface ScheduleConfig
        permits OneTimeConfig, RecurringConfig, WeeklyConfig, CronConfig {
}
