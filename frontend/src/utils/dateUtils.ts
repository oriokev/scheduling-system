export function formatDateTime(iso?: string): string {
  return iso ? new Date(iso).toLocaleString() : '—'
}
