import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { useForm, FormProvider } from 'react-hook-form'
import { TaskParamFields } from './TaskParamFields'
import type { ParameterSchema } from '../types/scheduling'

function Wrapper({ schema }: { schema: ParameterSchema[] }) {
  const methods = useForm({ defaultValues: { taskParams: {} } })
  return (
    <FormProvider {...methods}>
      <TaskParamFields schema={schema} />
    </FormProvider>
  )
}

describe('TaskParamFields', () => {
  it('renders nothing for an empty schema', () => {
    const { container } = render(<Wrapper schema={[]} />)
    expect(container).toBeEmptyDOMElement()
  })

  it('renders a text input for a field without options', () => {
    const schema: ParameterSchema[] = [
      { name: 'message', type: 'string', required: true, description: 'The message', options: undefined, defaultValue: undefined },
    ]
    render(<Wrapper schema={schema} />)
    expect(screen.getByPlaceholderText('The message')).toBeInTheDocument()
    expect(screen.queryByRole('combobox')).not.toBeInTheDocument()
  })

  it('renders a select for a field with options', () => {
    const schema: ParameterSchema[] = [
      { name: 'level', type: 'string', required: false, description: 'Log level', options: ['INFO', 'WARN', 'ERROR'], defaultValue: 'INFO' },
    ]
    render(<Wrapper schema={schema} />)
    const select = screen.getByRole('combobox')
    expect(select).toBeInTheDocument()
    expect(screen.getByText('INFO')).toBeInTheDocument()
    expect(screen.getByText('WARN')).toBeInTheDocument()
    expect(screen.getByText('ERROR')).toBeInTheDocument()
  })

  it('marks required fields with an asterisk', () => {
    const schema: ParameterSchema[] = [
      { name: 'url', type: 'string', required: true, description: 'Target URL', options: undefined, defaultValue: undefined },
    ]
    render(<Wrapper schema={schema} />)
    expect(screen.getByText('*')).toBeInTheDocument()
  })

  it('does not show asterisk for optional fields', () => {
    const schema: ParameterSchema[] = [
      { name: 'body', type: 'string', required: false, description: 'Body', options: undefined, defaultValue: undefined },
    ]
    render(<Wrapper schema={schema} />)
    expect(screen.queryByText('*')).not.toBeInTheDocument()
  })

  it('hides the blank — select — option when the field has a defaultValue', () => {
    const schema: ParameterSchema[] = [
      { name: 'method', type: 'string', required: false, description: 'HTTP method', options: ['GET', 'POST'], defaultValue: 'GET' },
    ]
    render(<Wrapper schema={schema} />)
    expect(screen.queryByText('— select —')).not.toBeInTheDocument()
  })

  it('shows the blank — select — option for optional fields without a defaultValue', () => {
    const schema: ParameterSchema[] = [
      { name: 'format', type: 'string', required: false, description: 'Format', options: ['JSON', 'XML'], defaultValue: undefined },
    ]
    render(<Wrapper schema={schema} />)
    expect(screen.getByText('— select —')).toBeInTheDocument()
  })

  it('renders multiple fields', () => {
    const schema: ParameterSchema[] = [
      { name: 'to',      type: 'string', required: true,  description: 'Recipient', options: undefined, defaultValue: undefined },
      { name: 'subject', type: 'string', required: true,  description: 'Subject',   options: undefined, defaultValue: undefined },
      { name: 'body',    type: 'string', required: false, description: 'Body',      options: undefined, defaultValue: undefined },
    ]
    render(<Wrapper schema={schema} />)
    expect(screen.getByPlaceholderText('Recipient')).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Subject')).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Body')).toBeInTheDocument()
  })
})
