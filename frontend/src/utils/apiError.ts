import axios from 'axios'

export function getApiErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data
    if (data?.message) return String(data.message)
    if (data?.error) return String(data.error)
    if (error.message) return error.message
  }
  if (error instanceof Error) return error.message
  return 'An unexpected error occurred'
}
