CREATE TABLE scheduling (
    id            VARCHAR(36)  NOT NULL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    description   TEXT,
    task_type     VARCHAR(50)  NOT NULL,
    task_params   TEXT         NOT NULL,
    schedule_type VARCHAR(50)  NOT NULL,
    schedule_config TEXT       NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL,
    last_run_at   DATETIME(6),
    next_run_at   DATETIME(6)
);
