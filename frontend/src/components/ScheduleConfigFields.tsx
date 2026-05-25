import type { UseFormRegister, UseFormWatch, FormState } from 'react-hook-form'
import type { SchedulingFormValues } from './SchedulingFormModal'

interface Props {
  register: UseFormRegister<SchedulingFormValues>
  watch: UseFormWatch<SchedulingFormValues>
  errors: FormState<SchedulingFormValues>['errors']
}

const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']

export function ScheduleConfigFields({ register, watch, errors }: Props) {
  const scheduleType = watch('scheduleType')

  return (
    <div className="space-y-4">
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Schedule Type</label>
        <select
          {...register('scheduleType', { required: 'Required' })}
          className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="ONE_TIME">One-Time</option>
          <option value="RECURRING">Recurring</option>
          <option value="WEEKLY">Weekly</option>
          <option value="CRON">Cron Expression</option>
        </select>
        {errors.scheduleType && <p className="text-red-500 text-xs mt-1">{errors.scheduleType.message}</p>}
      </div>

      {scheduleType === 'ONE_TIME' && (
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Run At</label>
          <input
            type="datetime-local"
            {...register('runAt', { required: 'Required' })}
            className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          {errors.runAt && <p className="text-red-500 text-xs mt-1">{errors.runAt.message}</p>}
        </div>
      )}

      {scheduleType === 'RECURRING' && (
        <div className="flex gap-3">
          <div className="flex-1">
            <label className="block text-sm font-medium text-gray-700 mb-1">Every</label>
            <input
              type="number"
              min={1}
              step={1}
              {...register('intervalValue', { required: 'Required', min: { value: 1, message: 'Min 1' } })}
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            {errors.intervalValue && <p className="text-red-500 text-xs mt-1">{errors.intervalValue.message}</p>}
          </div>
          <div className="flex-1">
            <label className="block text-sm font-medium text-gray-700 mb-1">Unit</label>
            <select
              {...register('intervalUnit', { required: 'Required' })}
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="MINUTES">Minutes</option>
              <option value="HOURS">Hours</option>
              <option value="DAYS">Days</option>
            </select>
          </div>
        </div>
      )}

      {scheduleType === 'WEEKLY' && (
        <div className="flex gap-3">
          <div className="flex-1">
            <label className="block text-sm font-medium text-gray-700 mb-1">Day of Week</label>
            <select
              {...register('dayOfWeek', { required: 'Required' })}
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              {DAYS.map(d => (
                <option key={d} value={d}>{d.charAt(0) + d.slice(1).toLowerCase()}</option>
              ))}
            </select>
          </div>
          <div className="flex-1">
            <label className="block text-sm font-medium text-gray-700 mb-1">Time (HH:mm)</label>
            <input
              type="time"
              {...register('weeklyTime', { required: 'Required' })}
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            {errors.weeklyTime && <p className="text-red-500 text-xs mt-1">{errors.weeklyTime.message}</p>}
          </div>
        </div>
      )}

      {scheduleType === 'CRON' && (
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Cron Expression
            <span className="text-gray-400 font-normal ml-2 text-xs">e.g. 0 0 9 ? * MON-FRI</span>
          </label>
          <input
            type="text"
            placeholder="0 0 9 ? * MON-FRI"
            {...register('cronExpression', { required: 'Required' })}
            className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm font-mono focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          {errors.cronExpression && <p className="text-red-500 text-xs mt-1">{errors.cronExpression.message}</p>}
        </div>
      )}
    </div>
  )
}
