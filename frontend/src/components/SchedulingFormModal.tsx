import { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { X } from 'lucide-react'
import { useTaskTypes, useCreateScheduling, useUpdateScheduling } from '../hooks/useSchedulings'
import { ScheduleConfigFields } from './ScheduleConfigFields'
import { TaskParamFields } from './TaskParamFields'
import type { Scheduling, SchedulingRequest, ScheduleConfig } from '../types/scheduling'

export interface SchedulingFormValues {
  name: string
  description: string
  taskType: string
  taskParams: Record<string, string>
  scheduleType: string
  // ONE_TIME
  runAt: string
  // RECURRING
  intervalValue: number
  intervalUnit: string
  // WEEKLY
  dayOfWeek: string
  weeklyTime: string
  // CRON
  cronExpression: string
}

interface Props {
  editing?: Scheduling
  onClose: () => void
}

export function SchedulingFormModal({ editing, onClose }: Props) {
  const { data: taskTypes = [] } = useTaskTypes()
  const createMutation = useCreateScheduling()
  const updateMutation = useUpdateScheduling()

  const {
    register, handleSubmit, watch, reset, formState: { errors, isSubmitting },
  } = useForm<SchedulingFormValues>({
    defaultValues: {
      scheduleType: 'RECURRING',
      intervalUnit: 'MINUTES',
      intervalValue: 5,
      dayOfWeek: 'MONDAY',
      weeklyTime: '09:00',
      taskType: 'LOG_TASK',
      taskParams: {},
    },
  })

  const selectedTaskType = watch('taskType')
  const currentSchema = taskTypes.find(t => t.value === selectedTaskType)?.schema ?? []

  useEffect(() => {
    if (!editing) return
    const cfg = editing.scheduleConfig
    reset({
      name: editing.name,
      description: editing.description ?? '',
      taskType: editing.taskType,
      taskParams: editing.taskParams,
      scheduleType: editing.scheduleType,
      runAt: cfg.type === 'ONE_TIME' ? cfg.runAt.slice(0, 16) : '',
      intervalValue: cfg.type === 'RECURRING' ? cfg.intervalValue : 5,
      intervalUnit: cfg.type === 'RECURRING' ? cfg.intervalUnit : 'MINUTES',
      dayOfWeek: cfg.type === 'WEEKLY' ? cfg.dayOfWeek : 'MONDAY',
      weeklyTime: cfg.type === 'WEEKLY' ? cfg.time : '09:00',
      cronExpression: cfg.type === 'CRON' ? cfg.expression : '',
    })
  }, [editing, reset])

  const buildScheduleConfig = (values: SchedulingFormValues): ScheduleConfig => {
    switch (values.scheduleType) {
      case 'ONE_TIME':
        return { type: 'ONE_TIME', runAt: new Date(values.runAt).toISOString().slice(0, 19) }
      case 'RECURRING':
        return { type: 'RECURRING', intervalValue: Number(values.intervalValue), intervalUnit: values.intervalUnit as any }
      case 'WEEKLY':
        return { type: 'WEEKLY', dayOfWeek: values.dayOfWeek, time: values.weeklyTime }
      case 'CRON':
        return { type: 'CRON', expression: values.cronExpression }
      default:
        throw new Error('Unknown schedule type')
    }
  }

  const onSubmit = async (values: SchedulingFormValues) => {
    const req: SchedulingRequest = {
      name: values.name,
      description: values.description || undefined,
      taskType: values.taskType as any,
      taskParams: values.taskParams ?? {},
      scheduleType: values.scheduleType as any,
      scheduleConfig: buildScheduleConfig(values),
    }

    if (editing) {
      await updateMutation.mutateAsync({ id: editing.id, req })
    } else {
      await createMutation.mutateAsync(req)
    }
    onClose()
  }

  const error = createMutation.error || updateMutation.error

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <div className="bg-white rounded-xl shadow-2xl w-full max-w-lg mx-4 max-h-[90vh] flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b">
          <h2 className="text-lg font-semibold text-gray-900">
            {editing ? 'Edit Scheduling' : 'New Scheduling'}
          </h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <X size={20} />
          </button>
        </div>

        {/* Body */}
        <form onSubmit={handleSubmit(onSubmit)} className="overflow-y-auto p-6 space-y-6 flex-1">
          {/* Basic info */}
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Name <span className="text-red-500">*</span></label>
              <input
                type="text"
                {...register('name', { required: 'Name is required' })}
                className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              {errors.name && <p className="text-red-500 text-xs mt-1">{errors.name.message}</p>}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
              <input
                type="text"
                {...register('description')}
                className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          {/* Task selection */}
          <div>
            <h3 className="text-sm font-semibold text-gray-900 mb-3 uppercase tracking-wide">Task</h3>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Task Type <span className="text-red-500">*</span></label>
              <select
                {...register('taskType', { required: 'Required' })}
                className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                {taskTypes.map(t => (
                  <option key={t.value} value={t.value}>{t.displayName}</option>
                ))}
              </select>
            </div>
          </div>

          {/* Task parameters */}
          {currentSchema.length > 0 && (
            <div>
              <h3 className="text-sm font-semibold text-gray-900 mb-3 uppercase tracking-wide">Parameters</h3>
              <TaskParamFields schema={currentSchema} register={register} errors={errors} />
            </div>
          )}

          {/* Schedule config */}
          <div>
            <h3 className="text-sm font-semibold text-gray-900 mb-3 uppercase tracking-wide">Schedule</h3>
            <ScheduleConfigFields register={register} watch={watch} errors={errors} />
          </div>

          {error && (
            <p className="text-red-600 text-sm bg-red-50 border border-red-200 rounded p-3">
              {(error as any)?.response?.data?.message ?? 'An error occurred'}
            </p>
          )}
        </form>

        {/* Footer */}
        <div className="flex justify-end gap-3 px-6 py-4 border-t bg-gray-50 rounded-b-xl">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
          >
            Cancel
          </button>
          <button
            type="submit"
            form="scheduling-form"
            onClick={handleSubmit(onSubmit)}
            disabled={isSubmitting}
            className="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700 disabled:opacity-50"
          >
            {isSubmitting ? 'Saving…' : editing ? 'Update' : 'Create'}
          </button>
        </div>
      </div>
    </div>
  )
}
