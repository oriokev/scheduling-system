import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { schedulingApi } from '../api/schedulingApi'
import type { SchedulingRequest } from '../types/scheduling'

const KEYS = {
  all: ['schedulings'] as const,
  taskTypes: ['task-types'] as const,
}

export function useSchedulings() {
  return useQuery({ queryKey: KEYS.all, queryFn: schedulingApi.list })
}

export function useTaskTypes() {
  return useQuery({ queryKey: KEYS.taskTypes, queryFn: schedulingApi.taskTypes })
}

export function useCreateScheduling() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (req: SchedulingRequest) => schedulingApi.create(req),
    onSuccess: () => qc.invalidateQueries({ queryKey: KEYS.all }),
  })
}

export function useUpdateScheduling() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, req }: { id: string; req: SchedulingRequest }) =>
      schedulingApi.update(id, req),
    onSuccess: () => qc.invalidateQueries({ queryKey: KEYS.all }),
  })
}

export function useDeleteScheduling() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: string) => schedulingApi.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: KEYS.all }),
  })
}

export function usePauseScheduling() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: string) => schedulingApi.pause(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: KEYS.all }),
  })
}

export function useResumeScheduling() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: string) => schedulingApi.resume(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: KEYS.all }),
  })
}
