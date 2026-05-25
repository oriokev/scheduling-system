package com.oriokev.schedulingsystem.service;

import com.oriokev.schedulingsystem.domain.Scheduling;
import com.oriokev.schedulingsystem.domain.ScheduleStatus;
import com.oriokev.schedulingsystem.domain.ScheduleType;
import com.oriokev.schedulingsystem.domain.schedule.OneTimeConfig;
import com.oriokev.schedulingsystem.exception.ResourceNotFoundException;
import com.oriokev.schedulingsystem.quartz.TriggerFactory;
import com.oriokev.schedulingsystem.repository.SchedulingRepository;
import com.oriokev.schedulingsystem.schema.TaskSchemaRegistry;
import com.oriokev.schedulingsystem.web.dto.SchedulingRequest;
import com.oriokev.schedulingsystem.web.dto.SchedulingResponse;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SchedulingService {

    private static final Logger log = LoggerFactory.getLogger(SchedulingService.class);

    private final SchedulingRepository repository;
    private final TaskSchemaRegistry schemaRegistry;
    private final TriggerFactory triggerFactory;
    private final Scheduler scheduler;

    public SchedulingService(SchedulingRepository repository,
                             TaskSchemaRegistry schemaRegistry,
                             TriggerFactory triggerFactory,
                             Scheduler scheduler) {
        this.repository = repository;
        this.schemaRegistry = schemaRegistry;
        this.triggerFactory = triggerFactory;
        this.scheduler = scheduler;
    }

    @Transactional(readOnly = true)
    public List<SchedulingResponse> findAll() {
        return repository.findAll().stream()
                .map(SchedulingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SchedulingResponse findById(UUID id) {
        return SchedulingResponse.from(load(id));
    }

    public SchedulingResponse create(SchedulingRequest request) {
        schemaRegistry.validateParams(request.taskType(), request.taskParams());

        Scheduling scheduling = new Scheduling();
        scheduling.setName(request.name());
        scheduling.setDescription(request.description());
        scheduling.setTaskType(request.taskType());
        scheduling.setTaskParams(request.taskParams());
        scheduling.setScheduleType(request.scheduleType());
        scheduling.setScheduleConfig(request.scheduleConfig());
        scheduling.setStatus(ScheduleStatus.ACTIVE);

        repository.save(scheduling);
        scheduleInQuartz(scheduling);

        log.info("Created scheduling '{}' ({})", scheduling.getName(), scheduling.getId());
        return SchedulingResponse.from(scheduling);
    }

    public SchedulingResponse update(UUID id, SchedulingRequest request) {
        Scheduling scheduling = load(id);
        schemaRegistry.validateParams(request.taskType(), request.taskParams());

        scheduling.setName(request.name());
        scheduling.setDescription(request.description());
        scheduling.setTaskType(request.taskType());
        scheduling.setTaskParams(request.taskParams());
        scheduling.setScheduleType(request.scheduleType());
        scheduling.setScheduleConfig(request.scheduleConfig());

        unscheduleFromQuartz(scheduling);
        if (scheduling.getStatus() == ScheduleStatus.ACTIVE) {
            scheduleInQuartz(scheduling);
        }

        repository.save(scheduling);
        log.info("Updated scheduling '{}'", scheduling.getId());
        return SchedulingResponse.from(scheduling);
    }

    public void delete(UUID id) {
        Scheduling scheduling = load(id);
        unscheduleFromQuartz(scheduling);
        repository.delete(scheduling);
        log.info("Deleted scheduling '{}'", id);
    }

    public SchedulingResponse pause(UUID id) {
        Scheduling scheduling = load(id);
        if (scheduling.getStatus() != ScheduleStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE schedulings can be paused");
        }
        try {
            scheduler.pauseJob(TriggerFactory.jobKey(id.toString()));
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to pause scheduler job: " + e.getMessage(), e);
        }
        scheduling.setStatus(ScheduleStatus.PAUSED);
        return SchedulingResponse.from(scheduling);
    }

    public SchedulingResponse resume(UUID id) {
        Scheduling scheduling = load(id);
        if (scheduling.getStatus() != ScheduleStatus.PAUSED) {
            throw new IllegalStateException("Only PAUSED schedulings can be resumed");
        }
        try {
            boolean exists = scheduler.checkExists(TriggerFactory.jobKey(id.toString()));
            if (!exists) {
                scheduleInQuartz(scheduling);
            } else {
                scheduler.resumeJob(TriggerFactory.jobKey(id.toString()));
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to resume scheduler job: " + e.getMessage(), e);
        }
        scheduling.setStatus(ScheduleStatus.ACTIVE);
        return SchedulingResponse.from(scheduling);
    }

    private void scheduleInQuartz(Scheduling scheduling) {
        if (scheduling.getScheduleType() == ScheduleType.ONE_TIME) {
            OneTimeConfig c = (OneTimeConfig) scheduling.getScheduleConfig();
            if (c.runAt().isBefore(LocalDateTime.now())) {
                log.warn("ONE_TIME scheduling '{}' is in the past, marking COMPLETED", scheduling.getId());
                scheduling.setStatus(ScheduleStatus.COMPLETED);
                return;
            }
        }
        try {
            JobDetail jobDetail = triggerFactory.buildJobDetail(scheduling);
            Trigger trigger     = triggerFactory.buildTrigger(scheduling);
            scheduler.scheduleJob(jobDetail, trigger);

            Date next = trigger.getNextFireTime();
            if (next != null) {
                scheduling.setNextRunAt(next.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to schedule job: " + e.getMessage(), e);
        }
    }

    private void unscheduleFromQuartz(Scheduling scheduling) {
        try {
            JobKey key = TriggerFactory.jobKey(scheduling.getId().toString());
            if (scheduler.checkExists(key)) {
                scheduler.deleteJob(key);
            }
        } catch (SchedulerException e) {
            log.warn("Could not remove Quartz job for {}: {}", scheduling.getId(), e.getMessage());
        }
    }

    private Scheduling load(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scheduling not found: " + id));
    }
}
