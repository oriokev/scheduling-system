# Scheduling System

A full-stack application for creating and managing scheduled task executions.

## Tech Stack

| Layer    | Technology |
|----------|-----------|
| Frontend | React 18, TypeScript, Vite, Tailwind CSS, TanStack Query, React Hook Form |
| Backend  | Java 21, Spring Boot 3.4, Spring Data JPA, Quartz Scheduler |
| Database | MySQL 8.0 |
| Infra    | Docker, Docker Compose |

---

## Running with Docker (recommended)

```bash
cd docker
docker compose up --build
```

| Service  | URL |
|----------|-----|
| Frontend | http://localhost:3000 |
| Backend  | http://localhost:8080 |
| MySQL    | localhost:3306 |

---

## Running locally

### Prerequisites
- Java 21
- Maven 3.9+
- Node 20+
- MySQL 8.0 running on localhost:3306

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

The backend reads connection settings from `application.properties`. Override with env vars:

```
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/scheduling_system?createDatabaseIfNotExist=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Opens at http://localhost:5173. API calls are proxied to `http://localhost:8080` via Vite's dev proxy.

---

## Running tests

```bash
cd backend
./mvnw test
```

Tests run against an in-memory H2 database (no MySQL required).

---

## Project Structure

```
scheduling-system/
├── backend/          Spring Boot application
│   ├── src/main/java/com/oriokev/schedulingsystem/
│   │   ├── domain/       JPA entity, enums, ScheduleConfig hierarchy
│   │   ├── converter/    JPA AttributeConverters for JSON columns
│   │   ├── schema/       Task parameter schema + registry
│   │   ├── repository/   Spring Data JPA repositories
│   │   ├── task/         TaskExecutor interface + 3 implementations
│   │   ├── quartz/       TriggerFactory, SchedulingJob
│   │   ├── service/      SchedulingService, QuartzSyncService
│   │   ├── web/          REST controllers + DTOs
│   │   ├── config/       QuartzConfig, CorsConfig
│   │   └── exception/    GlobalExceptionHandler
│   └── src/main/resources/
│       └── db/migration/   Flyway SQL migrations
├── frontend/         React + TypeScript application
│   └── src/
│       ├── api/        Axios API client
│       ├── components/ UI components
│       ├── hooks/      TanStack Query hooks
│       └── types/      TypeScript interfaces
└── docker/
    └── docker-compose.yml
```

---

## API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | /api/schedulings | List all schedulings |
| GET    | /api/schedulings/{id} | Get one scheduling |
| POST   | /api/schedulings | Create scheduling |
| PUT    | /api/schedulings/{id} | Update scheduling |
| DELETE | /api/schedulings/{id} | Delete scheduling |
| POST   | /api/schedulings/{id}/pause | Pause scheduling |
| POST   | /api/schedulings/{id}/resume | Resume scheduling |
| GET    | /api/task-types | List task types with schemas |
| GET    | /api/task-types/{type}/schema | Get parameter schema for a task type |

---

## Schedule Types

| Type | Configuration |
|------|--------------|
| ONE_TIME | `{ "type": "ONE_TIME", "runAt": "2026-06-01T10:00:00" }` |
| RECURRING | `{ "type": "RECURRING", "intervalValue": 30, "intervalUnit": "MINUTES" }` |
| WEEKLY | `{ "type": "WEEKLY", "dayOfWeek": "MONDAY", "time": "09:00" }` |
| CRON | `{ "type": "CRON", "expression": "0 0 9 ? * MON-FRI" }` |

---

## Predefined Tasks

| Task | Required Params | Optional Params |
|------|----------------|----------------|
| LOG_TASK | `message` | `level` (INFO/WARN/ERROR) |
| EMAIL_TASK | `to`, `subject` | `body` |
| HTTP_REQUEST_TASK | `url` | `method`, `body` |

Tasks are read-only — not stored in the database, registered at startup via `TaskSchemaRegistry`.

---

## Design Decisions

**Quartz RAM store + rehydration:** Quartz is configured with an in-memory store to avoid the overhead of 11 extra database tables. On startup, `QuartzSyncService` loads all `ACTIVE`/`PAUSED` schedulings from the database and re-registers them with Quartz. The `scheduling` table is the single source of truth.

**ScheduleConfig as sealed interface:** Using Java 21 sealed interfaces with Jackson polymorphism (`@JsonTypeInfo`) gives compile-time exhaustiveness checking in switch expressions (e.g. `TriggerFactory`). Adding a new schedule type forces handling everywhere.

**TEXT columns for JSON:** MySQL's `TEXT` type is used instead of `JSON` so the same Flyway migration runs on H2 (used in tests) without branching.

**Parameter schema in registry (not DB):** Task definitions are code, not data. The `TaskSchemaRegistry` is a Spring component holding static schemas. This avoids a separate `task` table for truly immutable, code-driven configuration.

**Confirmation on delete:** The delete button requires two clicks (the second click turns red) to prevent accidental deletion — no modal required.

---

## AI Tools Used

This project was built with **Claude Code (claude-sonnet-4-6)** used as a senior full-stack pair programmer. It was used to:

- Design the system architecture and data model
- Generate all Java backend source files (domain model, services, controllers, Quartz integration)
- Generate all React frontend files (components, hooks, API client, TypeScript types)
- Write unit and integration tests

All generated code was reviewed for correctness, security, and alignment with the assessment requirements.
