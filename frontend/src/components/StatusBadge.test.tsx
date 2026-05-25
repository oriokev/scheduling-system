import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { StatusBadge } from './StatusBadge'

describe('StatusBadge', () => {
  it.each(['ACTIVE', 'PAUSED', 'COMPLETED', 'FAILED'] as const)(
    'renders the %s label',
    (status) => {
      render(<StatusBadge status={status} />)
      expect(screen.getByText(status)).toBeInTheDocument()
    }
  )

  it('applies green style for ACTIVE', () => {
    render(<StatusBadge status="ACTIVE" />)
    expect(screen.getByText('ACTIVE')).toHaveClass('bg-green-100', 'text-green-800')
  })

  it('applies yellow style for PAUSED', () => {
    render(<StatusBadge status="PAUSED" />)
    expect(screen.getByText('PAUSED')).toHaveClass('bg-yellow-100', 'text-yellow-800')
  })

  it('applies red style for FAILED', () => {
    render(<StatusBadge status="FAILED" />)
    expect(screen.getByText('FAILED')).toHaveClass('bg-red-100', 'text-red-800')
  })

  it('applies blue style for COMPLETED', () => {
    render(<StatusBadge status="COMPLETED" />)
    expect(screen.getByText('COMPLETED')).toHaveClass('bg-blue-100', 'text-blue-800')
  })
})
