import { useState } from 'react'
import { Calendar } from 'lucide-react'
import { SchedulingTable } from './components/SchedulingTable'
import { SchedulingFormModal } from './components/SchedulingFormModal'
import type { Scheduling } from './types/scheduling'

export default function App() {
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState<Scheduling | undefined>()

  const openCreate = () => {
    setEditing(undefined)
    setModalOpen(true)
  }

  const openEdit = (s: Scheduling) => {
    setEditing(s)
    setModalOpen(true)
  }

  const closeModal = () => {
    setModalOpen(false)
    setEditing(undefined)
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 shadow-sm">
        <div className="max-w-7xl mx-auto px-6 py-4 flex items-center gap-3">
          <Calendar className="text-blue-600" size={24} />
          <div>
            <h1 className="text-xl font-bold text-gray-900">Scheduling System</h1>
            <p className="text-xs text-gray-400">Manage scheduled task executions</p>
          </div>
        </div>
      </header>

      {/* Main content */}
      <main className="max-w-7xl mx-auto px-6 py-8">
        <SchedulingTable onEdit={openEdit} onCreate={openCreate} />
      </main>

      {/* Modal */}
      {modalOpen && (
        <SchedulingFormModal editing={editing} onClose={closeModal} />
      )}
    </div>
  )
}
