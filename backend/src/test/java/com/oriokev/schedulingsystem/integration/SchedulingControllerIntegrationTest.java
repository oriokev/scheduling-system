package com.oriokev.schedulingsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oriokev.schedulingsystem.domain.ScheduleStatus;
import com.oriokev.schedulingsystem.domain.ScheduleType;
import com.oriokev.schedulingsystem.domain.TaskType;
import com.oriokev.schedulingsystem.domain.schedule.CronConfig;
import com.oriokev.schedulingsystem.domain.schedule.OneTimeConfig;
import com.oriokev.schedulingsystem.domain.schedule.RecurringConfig;
import com.oriokev.schedulingsystem.domain.IntervalUnit;
import com.oriokev.schedulingsystem.repository.SchedulingRepository;
import com.oriokev.schedulingsystem.web.dto.SchedulingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SchedulingControllerIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired SchedulingRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void createScheduling_logTask_returnsCreated() throws Exception {
        SchedulingRequest req = new SchedulingRequest(
                "Test Log",
                "desc",
                TaskType.LOG_TASK,
                Map.of("message", "hello"),
                ScheduleType.RECURRING,
                new RecurringConfig(5, IntervalUnit.MINUTES)
        );

        mvc.perform(post("/api/schedulings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Log"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.taskType").value("LOG_TASK"));
    }

    @Test
    void createScheduling_missingRequiredParam_returns400() throws Exception {
        SchedulingRequest req = new SchedulingRequest(
                "Bad Request",
                null,
                TaskType.LOG_TASK,
                Map.of(), // missing "message"
                ScheduleType.CRON,
                new CronConfig("0 * * * * ?")
        );

        mvc.perform(post("/api/schedulings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listSchedulings_returnsAll() throws Exception {
        createViaApi("First",  Map.of("message", "a"));
        createViaApi("Second", Map.of("message", "b"));

        mvc.perform(get("/api/schedulings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void deleteScheduling_returns204() throws Exception {
        String id = createViaApi("ToDelete", Map.of("message", "bye"));

        mvc.perform(delete("/api/schedulings/{id}", id))
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void updateScheduling_changesName() throws Exception {
        String id = createViaApi("Original", Map.of("message", "original"));

        SchedulingRequest update = new SchedulingRequest(
                "Updated",
                null,
                TaskType.LOG_TASK,
                Map.of("message", "updated"),
                ScheduleType.CRON,
                new CronConfig("0 * * * * ?")
        );

        mvc.perform(put("/api/schedulings/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void pauseAndResume_changesStatus() throws Exception {
        String id = createViaApi("Pauseable", Map.of("message", "pause me"));

        mvc.perform(post("/api/schedulings/{id}/pause", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAUSED"));

        mvc.perform(post("/api/schedulings/{id}/resume", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mvc.perform(get("/api/schedulings/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTaskTypes_returnsAllThree() throws Exception {
        mvc.perform(get("/api/task-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    private String createViaApi(String name, Map<String, String> params) throws Exception {
        SchedulingRequest req = new SchedulingRequest(
                name,
                null,
                TaskType.LOG_TASK,
                params,
                ScheduleType.RECURRING,
                new RecurringConfig(10, IntervalUnit.MINUTES)
        );
        String response = mvc.perform(post("/api/schedulings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }
}
