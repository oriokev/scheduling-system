import { useFormContext } from 'react-hook-form'
import type { ParameterSchema } from '../types/scheduling'
import type { SchedulingFormValues } from './SchedulingFormModal'

interface Props {
  schema: ParameterSchema[]
}

const inputClass = "w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"

export function TaskParamFields({ schema }: Props) {
  const { register, formState: { errors } } = useFormContext<SchedulingFormValues>()

  if (schema.length === 0) return null

  return (
    <div className="space-y-3">
      {schema.map(field => (
        <div key={field.name}>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            {field.name}
            {field.required && <span className="text-red-500 ml-1">*</span>}
            <span className="text-gray-400 font-normal ml-2 text-xs">{field.description}</span>
          </label>

          {field.options && field.options.length > 0 ? (
            <select
              {...register(`taskParams.${field.name}` as any, {
                required: field.required ? `${field.name} is required` : false,
              })}
              className={inputClass}
            >
              {!field.required && !field.defaultValue && <option value="">— select —</option>}
              {field.options.map(opt => (
                <option key={opt} value={opt}>{opt}</option>
              ))}
            </select>
          ) : (
            <input
              type="text"
              placeholder={field.description}
              {...register(`taskParams.${field.name}` as any, {
                required: field.required ? `${field.name} is required` : false,
              })}
              className={inputClass}
            />
          )}

          {(errors.taskParams as any)?.[field.name] && (
            <p className="text-red-500 text-xs mt-1">
              {(errors.taskParams as any)[field.name]?.message}
            </p>
          )}
        </div>
      ))}
    </div>
  )
}
