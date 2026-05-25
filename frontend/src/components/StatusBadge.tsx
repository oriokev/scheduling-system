import type { ScheduleStatus } from '../types/scheduling'

const styles: Record<ScheduleStatus, string> = {
  ACTIVE:    'bg-green-100 text-green-800',
  PAUSED:    'bg-yellow-100 text-yellow-800',
  COMPLETED: 'bg-blue-100 text-blue-800',
  FAILED:    'bg-red-100 text-red-800',
}

export function StatusBadge({ status }: { status: ScheduleStatus }) {
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${styles[status]}`}>
      {status}
    </span>
  )
}
