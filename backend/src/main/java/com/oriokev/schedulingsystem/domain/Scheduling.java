package com.oriokev.schedulingsystem.domain;

import com.oriokev.schedulingsystem.converter.ScheduleConfigConverter;
import com.oriokev.schedulingsystem.converter.TaskParamsConverter;
import com.oriokev.schedulingsystem.domain.schedule.ScheduleConfig;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "scheduling")
@Getter
@Setter
public class Scheduling {

    @Id
    @UuidGenerator
    @Column(length = 36)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, length = 50)
    private TaskType taskType;

    @Convert(converter = TaskParamsConverter.class)
    @Column(name = "task_params", columnDefinition = "TEXT", nullable = false)
    private Map<String, String> taskParams;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false, length = 50)
    private ScheduleType scheduleType;

    @Convert(converter = ScheduleConfigConverter.class)
    @Column(name = "schedule_config", columnDefinition = "TEXT", nullable = false)
    private ScheduleConfig scheduleConfig;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ScheduleStatus status = ScheduleStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_run_at")
    private LocalDateTime lastRunAt;

    @Column(name = "next_run_at")
    private LocalDateTime nextRunAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
