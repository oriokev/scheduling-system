package com.oriokev.schedulingsystem.quartz;

import com.oriokev.schedulingsystem.domain.Scheduling;
import com.oriokev.schedulingsystem.domain.ScheduleStatus;
import com.oriokev.schedulingsystem.domain.ScheduleType;
import com.oriokev.schedulingsystem.repository.SchedulingRepository;
import com.oriokev.schedulingsystem.task.TaskExecutor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SchedulingJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(SchedulingJob.class);

    @Autowired
    private SchedulingRepository schedulingRepository;

    @Autowired
    private List<TaskExecutor> executors;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String schedulingId = context.getMergedJobDataMap().getString("schedulingId");

        Scheduling scheduling = schedulingRepository.findById(UUID.fromString(schedulingId))
                .orElseThrow(() -> new JobExecutionException("Scheduling not found: " + schedulingId));

        if (scheduling.getStatus() == ScheduleStatus.PAUSED) {
            log.info("Skipping paused scheduling: {}", schedulingId);
            return;
        }

        Map<String, TaskExecutor> executorMap = executors.stream()
                .collect(Collectors.toMap(e -> e.getTaskType().name(), Function.identity()));

        TaskExecutor executor = executorMap.get(scheduling.getTaskType().name());
        if (executor == null) {
            throw new JobExecutionException("No executor found for task type: " + scheduling.getTaskType());
        }

        try {
            executor.execute(scheduling.getTaskParams());
            scheduling.setLastRunAt(LocalDateTime.now());

            if (scheduling.getScheduleType() == ScheduleType.ONE_TIME) {
                scheduling.setStatus(ScheduleStatus.COMPLETED);
            }

            schedulingRepository.save(scheduling);
        } catch (Exception e) {
            log.error("Failed executing scheduling {}: {}", schedulingId, e.getMessage(), e);
            scheduling.setStatus(ScheduleStatus.FAILED);
            schedulingRepository.save(scheduling);
            throw new JobExecutionException(e);
        }
    }
}
