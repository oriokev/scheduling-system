package com.oriokev.schedulingsystem.unit;

import com.oriokev.schedulingsystem.domain.*;
import com.oriokev.schedulingsystem.domain.schedule.*;
import com.oriokev.schedulingsystem.quartz.TriggerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class TriggerFactoryTest {

    private TriggerFactory factory;

    @BeforeEach
    void setUp() {
        factory = new TriggerFactory();
    }

    @Test
    void oneTimeTrigger_isSimpleTrigger_firesOnce() {
        Scheduling s = scheduling(ScheduleType.ONE_TIME,
                new OneTimeConfig(LocalDateTime.now().plusHours(1)));

        Trigger trigger = factory.buildTrigger(s);

        assertThat(trigger).isInstanceOf(SimpleTrigger.class);
        SimpleTrigger st = (SimpleTrigger) trigger;
        assertThat(st.getRepeatCount()).isEqualTo(0);
    }

    @Test
    void recurringTrigger_minuteInterval_repeatsForever() {
        Scheduling s = scheduling(ScheduleType.RECURRING,
                new RecurringConfig(30, IntervalUnit.MINUTES));

        Trigger trigger = factory.buildTrigger(s);

        assertThat(trigger).isInstanceOf(SimpleTrigger.class);
        SimpleTrigger st = (SimpleTrigger) trigger;
        assertThat(st.getRepeatCount()).isEqualTo(SimpleTrigger.REPEAT_INDEFINITELY);
        assertThat(st.getRepeatInterval()).isEqualTo(30L * 60 * 1000);
    }

    @Test
    void recurringTrigger_hourInterval() {
        Scheduling s = scheduling(ScheduleType.RECURRING,
                new RecurringConfig(2, IntervalUnit.HOURS));

        Trigger trigger = factory.buildTrigger(s);

        SimpleTrigger st = (SimpleTrigger) trigger;
        assertThat(st.getRepeatInterval()).isEqualTo(2L * 60 * 60 * 1000);
    }

    @Test
    void weeklyTrigger_isCronTrigger() {
        Scheduling s = scheduling(ScheduleType.WEEKLY,
                new WeeklyConfig("MONDAY", "09:00"));

        Trigger trigger = factory.buildTrigger(s);

        assertThat(trigger).isInstanceOf(CronTrigger.class);
    }

    @Test
    void cronTrigger_usesProvidedExpression() {
        String expression = "0 0 12 ? * MON-FRI";
        Scheduling s = scheduling(ScheduleType.CRON,
                new CronConfig(expression));

        Trigger trigger = factory.buildTrigger(s);

        assertThat(trigger).isInstanceOf(CronTrigger.class);
        CronTrigger ct = (CronTrigger) trigger;
        assertThat(ct.getCronExpression()).isEqualTo(expression);
    }

    @Test
    void jobDetail_containsSchedulingId() {
        Scheduling s = scheduling(ScheduleType.CRON, new CronConfig("0 * * * * ?"));

        JobDetail detail = factory.buildJobDetail(s);

        assertThat(detail.getJobDataMap().getString("schedulingId"))
                .isEqualTo(s.getId().toString());
    }

    private Scheduling scheduling(ScheduleType type, ScheduleConfig config) {
        Scheduling s = new Scheduling();
        // set id via reflection since @UuidGenerator won't fire without JPA context
        try {
            var f = Scheduling.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(s, UUID.randomUUID());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        s.setScheduleType(type);
        s.setScheduleConfig(config);
        return s;
    }
}
