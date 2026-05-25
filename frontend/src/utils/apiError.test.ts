import { describe, it, expect } from 'vitest'
import { AxiosError } from 'axios'
import { getApiErrorMessage } from './apiError'

function makeAxiosError(data?: object, message = 'request failed'): AxiosError {
  const err = new AxiosError(message)
  if (data) {
    err.response = {
      data,
      status: 400,
      statusText: 'Bad Request',
      headers: {},
      config: { headers: {} } as any,
    }
  }
  return err
}

describe('getApiErrorMessage', () => {
  it('extracts message field from axios error response', () => {
    const err = makeAxiosError({ message: 'Scheduling not found' })
    expect(getApiErrorMessage(err)).toBe('Scheduling not found')
  })

  it('falls back to error field when message is absent', () => {
    const err = makeAxiosError({ error: 'Not Found' })
    expect(getApiErrorMessage(err)).toBe('Not Found')
  })

  it('falls back to axios message when response has no message or error', () => {
    const err = makeAxiosError({ status: 500 }, 'Network Error')
    expect(getApiErrorMessage(err)).toBe('Network Error')
  })

  it('extracts message from a plain Error', () => {
    expect(getApiErrorMessage(new Error('Something broke'))).toBe('Something broke')
  })

  it('returns generic message for unknown error types', () => {
    expect(getApiErrorMessage('oops')).toBe('An unexpected error occurred')
    expect(getApiErrorMessage(null)).toBe('An unexpected error occurred')
    expect(getApiErrorMessage(42)).toBe('An unexpected error occurred')
  })
})
