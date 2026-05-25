import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { SchedulingTable } from './SchedulingTable'
import * as hooks from '../hooks/useSchedulings'
import type { Scheduling } from '../types/scheduling'

vi.mock('../hooks/useSchedulings', () => ({
  useSchedulings:     vi.fn(),
  useDeleteScheduling: vi.fn(),
  usePauseScheduling:  vi.fn(),
  useResumeScheduling: vi.fn(),
}))

const activeScheduling: Scheduling = {
  id: 'abc-123',
  name: 'Daily Report',
  taskType: 'LOG_TASK',
  taskTypeDisplayName: 'Log Task',
  taskParams: { message: 'hello' },
  scheduleType: 'RECURRING',
  scheduleConfig: { type: 'RECURRING', intervalValue: 5, intervalUnit: 'MINUTES' },
  status: 'ACTIVE',
  createdAt: '2024-01-01T00:00:00',
  updatedAt: '2024-01-01T00:00:00',
}

const onEdit = vi.fn()
const onCreate = vi.fn()

beforeEach(() => {
  vi.mocked(hooks.useDeleteScheduling).mockReturnValue({ mutateAsync: vi.fn().mockResolvedValue(undefined) } as any)
  vi.mocked(hooks.usePauseScheduling).mockReturnValue({ mutate: vi.fn() } as any)
  vi.mocked(hooks.useResumeScheduling).mockReturnValue({ mutate: vi.fn() } as any)
})

describe('SchedulingTable', () => {
  it('shows loading state', () => {
    vi.mocked(hooks.useSchedulings).mockReturnValue({ data: undefined, isLoading: true, error: null, refetch: vi.fn() } as any)
    render(<SchedulingTable onEdit={onEdit} onCreate={onCreate} />)
    expect(screen.getByText(/loading/i)).toBeInTheDocument()
  })

  it('shows error state when fetch fails', () => {
    vi.mocked(hooks.useSchedulings).mockReturnValue({ data: undefined, isLoading: false, error: new Error('fail'), refetch: vi.fn() } as any)
    render(<SchedulingTable onEdit={onEdit} onCreate={onCreate} />)
    expect(screen.getByText(/failed to load/i)).toBeInTheDocument()
  })

  it('shows empty state when there are no schedulings', () => {
    vi.mocked(hooks.useSchedulings).mockReturnValue({ data: [], isLoading: false, error: null, refetch: vi.fn() } as any)
    render(<SchedulingTable onEdit={onEdit} onCreate={onCreate} />)
    expect(screen.getByText(/no schedulings yet/i)).toBeInTheDocument()
  })

  it('renders a row for each scheduling', () => {
    const second: Scheduling = { ...activeScheduling, id: 'def-456', name: 'Weekly Sync' }
    vi.mocked(hooks.useSchedulings).mockReturnValue({ data: [activeScheduling, second], isLoading: false, error: null, refetch: vi.fn() } as any)
    render(<SchedulingTable onEdit={onEdit} onCreate={onCreate} />)
    expect(screen.getByText('Daily Report')).toBeInTheDocument()
    expect(screen.getByText('Weekly Sync')).toBeInTheDocument()
    expect(screen.getByText('Schedulings')).toBeInTheDocument()
  })

  it('calls onEdit when the edit button is clicked', () => {
    vi.mocked(hooks.useSchedulings).mockReturnValue({ data: [activeScheduling], isLoading: false, error: null, refetch: vi.fn() } as any)
    render(<SchedulingTable onEdit={onEdit} onCreate={onCreate} />)
    fireEvent.click(screen.getByLabelText('Edit'))
    expect(onEdit).toHaveBeenCalledWith(activeScheduling)
  })

  it('calls onCreate when the New Scheduling button is clicked', () => {
    vi.mocked(hooks.useSchedulings).mockReturnValue({ data: [], isLoading: false, error: null, refetch: vi.fn() } as any)
    render(<SchedulingTable onEdit={onEdit} onCreate={onCreate} />)
    fireEvent.click(screen.getByText('New Scheduling'))
    expect(onCreate).toHaveBeenCalled()
  })

  it('shows pause button for ACTIVE schedulings', () => {
    vi.mocked(hooks.useSchedulings).mockReturnValue({ data: [activeScheduling], isLoading: false, error: null, refetch: vi.fn() } as any)
    render(<SchedulingTable onEdit={onEdit} onCreate={onCreate} />)
    expect(screen.getByLabelText('Pause')).toBeInTheDocument()
    expect(screen.queryByLabelText('Resume')).not.toBeInTheDocument()
  })

  it('shows resume button for PAUSED schedulings', () => {
    const paused: Scheduling = { ...activeScheduling, status: 'PAUSED' }
    vi.mocked(hooks.useSchedulings).mockReturnValue({ data: [paused], isLoading: false, error: null, refetch: vi.fn() } as any)
    render(<SchedulingTable onEdit={onEdit} onCreate={onCreate} />)
    expect(screen.getByLabelText('Resume')).toBeInTheDocument()
    expect(screen.queryByLabelText('Pause')).not.toBeInTheDocument()
  })

  it('requires a second click to confirm deletion', async () => {
    const mutateAsync = vi.fn().mockResolvedValue(undefined)
    vi.mocked(hooks.useDeleteScheduling).mockReturnValue({ mutateAsync } as any)
    vi.mocked(hooks.useSchedulings).mockReturnValue({ data: [activeScheduling], isLoading: false, error: null, refetch: vi.fn() } as any)

    render(<SchedulingTable onEdit={onEdit} onCreate={onCreate} />)

    fireEvent.click(screen.getByLabelText('Delete'))
    expect(mutateAsync).not.toHaveBeenCalled()

    await waitFor(() => screen.getByLabelText('Click again to confirm delete'))
    fireEvent.click(screen.getByLabelText('Click again to confirm delete'))

    await waitFor(() => expect(mutateAsync).toHaveBeenCalledWith('abc-123'))
  })

  it('calls pause mutation when pause is clicked', () => {
    const mutate = vi.fn()
    vi.mocked(hooks.usePauseScheduling).mockReturnValue({ mutate } as any)
    vi.mocked(hooks.useSchedulings).mockReturnValue({ data: [activeScheduling], isLoading: false, error: null, refetch: vi.fn() } as any)

    render(<SchedulingTable onEdit={onEdit} onCreate={onCreate} />)
    fireEvent.click(screen.getByLabelText('Pause'))
    expect(mutate).toHaveBeenCalledWith('abc-123')
  })

  it('calls resume mutation when resume is clicked', () => {
    const mutate = vi.fn()
    vi.mocked(hooks.useResumeScheduling).mockReturnValue({ mutate } as any)
    const paused: Scheduling = { ...activeScheduling, status: 'PAUSED' }
    vi.mocked(hooks.useSchedulings).mockReturnValue({ data: [paused], isLoading: false, error: null, refetch: vi.fn() } as any)

    render(<SchedulingTable onEdit={onEdit} onCreate={onCreate} />)
    fireEvent.click(screen.getByLabelText('Resume'))
    expect(mutate).toHaveBeenCalledWith('abc-123')
  })
})
