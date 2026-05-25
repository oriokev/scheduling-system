import axios from 'axios'
import type { Scheduling, SchedulingRequest, TaskTypeInfo } from '../types/scheduling'

const client = axios.create({ baseURL: '/api' })

export const schedulingApi = {
  list: (): Promise<Scheduling[]> =>
    client.get<Scheduling[]>('/schedulings').then(r => r.data),

  get: (id: string): Promise<Scheduling> =>
    client.get<Scheduling>(`/schedulings/${id}`).then(r => r.data),

  create: (req: SchedulingRequest): Promise<Scheduling> =>
    client.post<Scheduling>('/schedulings', req).then(r => r.data),

  update: (id: string, req: SchedulingRequest): Promise<Scheduling> =>
    client.put<Scheduling>(`/schedulings/${id}`, req).then(r => r.data),

  delete: (id: string): Promise<void> =>
    client.delete(`/schedulings/${id}`).then(() => undefined),

  pause: (id: string): Promise<Scheduling> =>
    client.post<Scheduling>(`/schedulings/${id}/pause`).then(r => r.data),

  resume: (id: string): Promise<Scheduling> =>
    client.post<Scheduling>(`/schedulings/${id}/resume`).then(r => r.data),

  taskTypes: (): Promise<TaskTypeInfo[]> =>
    client.get<TaskTypeInfo[]>('/task-types').then(r => r.data),
}
