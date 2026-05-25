package com.oriokev.schedulingsystem.service;

import com.oriokev.schedulingsystem.domain.Scheduling;
import com.oriokev.schedulingsystem.domain.ScheduleStatus;
import com.oriokev.schedulingsystem.domain.ScheduleType;
import com.oriokev.schedulingsystem.domain.schedule.OneTimeConfig;
import com.oriokev.schedulingsystem.quartz.TriggerFactory;
import com.oriokev.schedulingsystem.repository.SchedulingRepository;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class QuartzSyncService {

    private static final Logger log = LoggerFactory.getLogger(QuartzSyncService.class);

    private final Scheduler scheduler;
    private final SchedulingRepository schedulingRepository;
    private final TriggerFactory triggerFactory;

    public QuartzSyncService(Scheduler scheduler,
                             SchedulingRepository schedulingRepository,
                             TriggerFactory triggerFactory) {
        this.scheduler = scheduler;
        this.schedulingRepository = schedulingRepository;
        this.triggerFactory = triggerFactory;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void rehydrate() {
        List<Scheduling> active = schedulingRepository.findAllByStatusIn(
                List.of(ScheduleStatus.ACTIVE, ScheduleStatus.PAUSED));

        log.info("Rehydrating {} schedulings into Quartz", active.size());

        for (Scheduling s : active) {
            try {
                if (s.getScheduleType() == ScheduleType.ONE_TIME) {
                    OneTimeConfig c = (OneTimeConfig) s.getScheduleConfig();
                    if (c.runAt().isBefore(LocalDateTime.now())) {
                        log.info("Skipping past ONE_TIME scheduling: {}", s.getId());
                        s.setStatus(ScheduleStatus.COMPLETED);
                        schedulingRepository.save(s);
                        continue;
                    }
                }

                JobDetail jobDetail = triggerFactory.buildJobDetail(s);
                Trigger trigger     = triggerFactory.buildTrigger(s);
                scheduler.scheduleJob(jobDetail, trigger);

                if (s.getStatus() == ScheduleStatus.PAUSED) {
                    scheduler.pauseJob(TriggerFactory.jobKey(s.getId().toString()));
                }

                updateNextRunAt(s, trigger);
                schedulingRepository.save(s);

                log.info("Rehydrated scheduling: {} ({})", s.getName(), s.getId());
            } catch (SchedulerException e) {
                log.error("Failed to rehydrate scheduling {}: {}", s.getId(), e.getMessage());
            }
        }
    }

    private void updateNextRunAt(Scheduling s, Trigger trigger) {
        Date next = trigger.getNextFireTime();
        if (next != null) {
            s.setNextRunAt(next.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime());
        }
    }
}
