package com.oriokev.schedulingsystem.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oriokev.schedulingsystem.domain.IntervalUnit;
import com.oriokev.schedulingsystem.domain.schedule.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class ScheduleConfigSerializationTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void oneTimeConfig_roundTrip() throws Exception {
        OneTimeConfig original = new OneTimeConfig(LocalDateTime.of(2026, 6, 1, 10, 0));
        String json = mapper.writeValueAsString(original);

        ScheduleConfig deserialized = mapper.readValue(json, ScheduleConfig.class);

        assertThat(deserialized).isInstanceOf(OneTimeConfig.class);
        assertThat(((OneTimeConfig) deserialized).runAt()).isEqualTo(original.runAt());
    }

    @Test
    void recurringConfig_roundTrip() throws Exception {
        RecurringConfig original = new RecurringConfig(15, IntervalUnit.MINUTES);
        String json = mapper.writeValueAsString(original);

        ScheduleConfig deserialized = mapper.readValue(json, ScheduleConfig.class);

        assertThat(deserialized).isInstanceOf(RecurringConfig.class);
        RecurringConfig result = (RecurringConfig) deserialized;
        assertThat(result.intervalValue()).isEqualTo(15);
        assertThat(result.intervalUnit()).isEqualTo(IntervalUnit.MINUTES);
    }

    @Test
    void weeklyConfig_roundTrip() throws Exception {
        WeeklyConfig original = new WeeklyConfig("FRIDAY", "14:30");
        String json = mapper.writeValueAsString(original);

        ScheduleConfig deserialized = mapper.readValue(json, ScheduleConfig.class);

        assertThat(deserialized).isInstanceOf(WeeklyConfig.class);
        WeeklyConfig result = (WeeklyConfig) deserialized;
        assertThat(result.dayOfWeek()).isEqualTo("FRIDAY");
        assertThat(result.time()).isEqualTo("14:30");
    }

    @Test
    void cronConfig_roundTrip() throws Exception {
        CronConfig original = new CronConfig("0 0 8 ? * MON-FRI");
        String json = mapper.writeValueAsString(original);

        ScheduleConfig deserialized = mapper.readValue(json, ScheduleConfig.class);

        assertThat(deserialized).isInstanceOf(CronConfig.class);
        assertThat(((CronConfig) deserialized).expression()).isEqualTo(original.expression());
    }

    @Test
    void json_containsTypeDiscriminator() throws Exception {
        CronConfig config = new CronConfig("0 * * * * ?");
        String json = mapper.writeValueAsString(config);

        assertThat(json).contains("\"type\":\"CRON\"");
    }
}
