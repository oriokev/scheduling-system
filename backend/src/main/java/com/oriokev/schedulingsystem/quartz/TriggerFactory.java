package com.oriokev.schedulingsystem.quartz;

import com.oriokev.schedulingsystem.domain.Scheduling;
import com.oriokev.schedulingsystem.domain.schedule.*;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Component
public class TriggerFactory {

    private static final String JOB_GROUP     = "scheduling-jobs";
    private static final String TRIGGER_GROUP = "scheduling-triggers";

    private static final Map<String, Integer> DAY_OF_WEEK_MAP = Map.of(
            "MONDAY",    Calendar.MONDAY,
            "TUESDAY",   Calendar.TUESDAY,
            "WEDNESDAY", Calendar.WEDNESDAY,
            "THURSDAY",  Calendar.THURSDAY,
            "FRIDAY",    Calendar.FRIDAY,
            "SATURDAY",  Calendar.SATURDAY,
            "SUNDAY",    Calendar.SUNDAY
    );

    public JobDetail buildJobDetail(Scheduling scheduling) {
        return JobBuilder.newJob(SchedulingJob.class)
                .withIdentity(scheduling.getId().toString(), JOB_GROUP)
                .usingJobData("schedulingId", scheduling.getId().toString())
                .storeDurably()
                .build();
    }

    public Trigger buildTrigger(Scheduling scheduling) {
        TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger()
                .withIdentity("trigger-" + scheduling.getId(), TRIGGER_GROUP);

        return switch (scheduling.getScheduleConfig()) {
            case OneTimeConfig c   -> buildOneTimeTrigger(builder, c);
            case RecurringConfig c -> buildRecurringTrigger(builder, c);
            case WeeklyConfig c    -> buildWeeklyTrigger(builder, c);
            case CronConfig c      -> buildCronTrigger(builder, c);
        };
    }

    private Trigger buildOneTimeTrigger(TriggerBuilder<Trigger> builder, OneTimeConfig config) {
        Date runAt = Date.from(config.runAt().atZone(ZoneId.systemDefault()).toInstant());
        return builder
                .startAt(runAt)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withMisfireHandlingInstructionFireNow())
                .build();
    }

    private Trigger buildRecurringTrigger(TriggerBuilder<Trigger> builder, RecurringConfig config) {
        SimpleScheduleBuilder schedule = switch (config.intervalUnit()) {
            case MINUTES -> SimpleScheduleBuilder.repeatMinutelyForever(config.intervalValue());
            case HOURS   -> SimpleScheduleBuilder.repeatHourlyForever(config.intervalValue());
            case DAYS    -> SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInHours(config.intervalValue() * 24)
                    .repeatForever();
        };
        return builder.withSchedule(schedule).startNow().build();
    }

    private Trigger buildWeeklyTrigger(TriggerBuilder<Trigger> builder, WeeklyConfig config) {
        Integer calDay = DAY_OF_WEEK_MAP.getOrDefault(config.dayOfWeek().toUpperCase(), Calendar.MONDAY);
        LocalTime time = LocalTime.parse(config.time());
        return builder
                .withSchedule(CronScheduleBuilder.weeklyOnDayAndHourAndMinute(
                        calDay, time.getHour(), time.getMinute()))
                .build();
    }

    private Trigger buildCronTrigger(TriggerBuilder<Trigger> builder, CronConfig config) {
        return builder
                .withSchedule(CronScheduleBuilder.cronSchedule(config.expression()))
                .build();
    }

    public static JobKey jobKey(String schedulingId) {
        return JobKey.jobKey(schedulingId, JOB_GROUP);
    }

    public static TriggerKey triggerKey(String schedulingId) {
        return TriggerKey.triggerKey("trigger-" + schedulingId, TRIGGER_GROUP);
    }
}
