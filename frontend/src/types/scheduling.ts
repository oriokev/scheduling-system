export type ScheduleType = 'ONE_TIME' | 'RECURRING' | 'WEEKLY' | 'CRON'
export type TaskType     = 'LOG_TASK' | 'EMAIL_TASK' | 'HTTP_REQUEST_TASK'
export type ScheduleStatus = 'ACTIVE' | 'PAUSED' | 'COMPLETED' | 'FAILED'
export type IntervalUnit  = 'MINUTES' | 'HOURS' | 'DAYS'

export interface OneTimeConfig {
  type: 'ONE_TIME'
  runAt: string // ISO datetime
}

export interface RecurringConfig {
  type: 'RECURRING'
  intervalValue: number
  intervalUnit: IntervalUnit
}

export interface WeeklyConfig {
  type: 'WEEKLY'
  dayOfWeek: string
  time: string // HH:mm
}

export interface CronConfig {
  type: 'CRON'
  expression: string
}

export type ScheduleConfig = OneTimeConfig | RecurringConfig | WeeklyConfig | CronConfig

export interface ParameterSchema {
  name: string
  type: string
  required: boolean
  description: string
}

export interface TaskTypeInfo {
  value: TaskType
  displayName: string
  schema: ParameterSchema[]
}

export interface Scheduling {
  id: string
  name: string
  description?: string
  taskType: TaskType
  taskTypeDisplayName: string
  taskParams: Record<string, string>
  scheduleType: ScheduleType
  scheduleConfig: ScheduleConfig
  status: ScheduleStatus
  createdAt: string
  updatedAt: string
  lastRunAt?: string
  nextRunAt?: string
}

export interface SchedulingRequest {
  name: string
  description?: string
  taskType: TaskType
  taskParams: Record<string, string>
  scheduleType: ScheduleType
  scheduleConfig: ScheduleConfig
}
