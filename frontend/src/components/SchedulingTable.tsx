import { useState } from 'react'
import { Pencil, Trash2, PauseCircle, PlayCircle, Plus, RefreshCw } from 'lucide-react'
import { StatusBadge } from './StatusBadge'
import { useSchedulings, useDeleteScheduling, usePauseScheduling, useResumeScheduling } from '../hooks/useSchedulings'
import type { Scheduling } from '../types/scheduling'

interface Props {
  onEdit: (s: Scheduling) => void
  onCreate: () => void
}

function formatDate(iso?: string) {
  if (!iso) return '—'
  return new Date(iso).toLocaleString()
}

function scheduleLabel(s: Scheduling): string {
  const cfg = s.scheduleConfig
  switch (cfg.type) {
    case 'ONE_TIME':   return `Once at ${formatDate(cfg.runAt)}`
    case 'RECURRING':  return `Every ${cfg.intervalValue} ${cfg.intervalUnit.toLowerCase()}`
    case 'WEEKLY':     return `${cfg.dayOfWeek.charAt(0) + cfg.dayOfWeek.slice(1).toLowerCase()} at ${cfg.time}`
    case 'CRON':       return cfg.expression
  }
}

export function SchedulingTable({ onEdit, onCreate }: Props) {
  const { data: schedulings = [], isLoading, error, refetch } = useSchedulings()
  const deleteMutation  = useDeleteScheduling()
  const pauseMutation   = usePauseScheduling()
  const resumeMutation  = useResumeScheduling()

  const [confirmDelete, setConfirmDelete] = useState<string | null>(null)

  const handleDelete = async (id: string) => {
    if (confirmDelete === id) {
      await deleteMutation.mutateAsync(id)
      setConfirmDelete(null)
    } else {
      setConfirmDelete(id)
    }
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64 text-gray-400">
        Loading schedulings…
      </div>
    )
  }

  if (error) {
    return (
      <div className="text-red-600 bg-red-50 border border-red-200 rounded-lg p-4">
        Failed to load schedulings. Is the backend running?
      </div>
    )
  }

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200">
      {/* Toolbar */}
      <div className="flex items-center justify-between px-6 py-4 border-b">
        <h2 className="text-base font-semibold text-gray-900">
          Schedulings <span className="text-gray-400 font-normal text-sm">({schedulings.length})</span>
        </h2>
        <div className="flex gap-2">
          <button
            onClick={() => refetch()}
            className="flex items-center gap-1.5 px-3 py-1.5 text-sm text-gray-600 border border-gray-300 rounded-md hover:bg-gray-50"
          >
            <RefreshCw size={14} />
            Refresh
          </button>
          <button
            onClick={onCreate}
            className="flex items-center gap-1.5 px-3 py-1.5 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
          >
            <Plus size={14} />
            New Scheduling
          </button>
        </div>
      </div>

      {/* Table */}
      {schedulings.length === 0 ? (
        <div className="text-center text-gray-400 py-16">
          No schedulings yet. Create one to get started.
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-gray-50 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                <th className="px-6 py-3">Name</th>
                <th className="px-6 py-3">Task</th>
                <th className="px-6 py-3">Schedule</th>
                <th className="px-6 py-3">Status</th>
                <th className="px-6 py-3">Next Run</th>
                <th className="px-6 py-3">Last Run</th>
                <th className="px-6 py-3">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {schedulings.map(s => (
                <tr key={s.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 font-medium text-gray-900">
                    {s.name}
                    {s.description && (
                      <p className="text-xs text-gray-400 font-normal mt-0.5">{s.description}</p>
                    )}
                  </td>
                  <td className="px-6 py-4 text-gray-600">{s.taskTypeDisplayName}</td>
                  <td className="px-6 py-4 text-gray-600 font-mono text-xs">{scheduleLabel(s)}</td>
                  <td className="px-6 py-4"><StatusBadge status={s.status} /></td>
                  <td className="px-6 py-4 text-gray-500 text-xs">{formatDate(s.nextRunAt)}</td>
                  <td className="px-6 py-4 text-gray-500 text-xs">{formatDate(s.lastRunAt)}</td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-1">
                      {s.status === 'ACTIVE' && (
                        <button
                          onClick={() => pauseMutation.mutate(s.id)}
                          className="p-1.5 text-yellow-600 hover:bg-yellow-50 rounded"
                          title="Pause"
                          aria-label="Pause"
                        >
                          <PauseCircle size={16} />
                        </button>
                      )}
                      {s.status === 'PAUSED' && (
                        <button
                          onClick={() => resumeMutation.mutate(s.id)}
                          className="p-1.5 text-green-600 hover:bg-green-50 rounded"
                          title="Resume"
                          aria-label="Resume"
                        >
                          <PlayCircle size={16} />
                        </button>
                      )}
                      <button
                        onClick={() => onEdit(s)}
                        className="p-1.5 text-blue-600 hover:bg-blue-50 rounded"
                        title="Edit"
                        aria-label="Edit"
                      >
                        <Pencil size={16} />
                      </button>
                      <button
                        onClick={() => handleDelete(s.id)}
                        className={`p-1.5 rounded ${
                          confirmDelete === s.id
                            ? 'text-white bg-red-500 hover:bg-red-600'
                            : 'text-red-500 hover:bg-red-50'
                        }`}
                        title={confirmDelete === s.id ? 'Click again to confirm' : 'Delete'}
                        aria-label={confirmDelete === s.id ? 'Click again to confirm delete' : 'Delete'}
                      >
                        <Trash2 size={16} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
