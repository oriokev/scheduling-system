import { describe, it, expect } from 'vitest'
import { formatDateTime } from './dateUtils'

describe('formatDateTime', () => {
  it('returns — for undefined', () => {
    expect(formatDateTime(undefined)).toBe('—')
  })

  it('returns — for empty string', () => {
    expect(formatDateTime('')).toBe('—')
  })

  it('returns a non-empty string for a valid ISO date', () => {
    const result = formatDateTime('2024-01-15T09:00:00.000Z')
    expect(result).toBeTruthy()
    expect(result).not.toBe('—')
  })

  it('produces different output for different dates', () => {
    const a = formatDateTime('2024-01-01T00:00:00.000Z')
    const b = formatDateTime('2024-06-15T12:00:00.000Z')
    expect(a).not.toBe(b)
  })
})
