// Testes unitários para useLoading composable
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { useLoading } from '../useLoading'

describe('useLoading', () => {
  let loading

  beforeEach(() => {
    loading = useLoading()
  })

  describe('Estado inicial', () => {
    it('deve ter loading como false inicialmente', () => {
      expect(loading.loading.value).toBe(false)
    })

    it('deve ter error como null inicialmente', () => {
      expect(loading.error.value).toBeNull()
    })
  })

  describe('execute', () => {
    it('deve definir loading como true durante execução', async () => {
      const asyncFn = vi.fn(() => new Promise(resolve => setTimeout(() => resolve('result'), 100)))

      const promise = loading.execute(asyncFn)

      // Verificar que loading foi definido como true imediatamente
      expect(loading.loading.value).toBe(true)
      expect(loading.error.value).toBeNull()

      await promise

      // Verificar que loading foi definido como false após conclusão
      expect(loading.loading.value).toBe(false)
      expect(asyncFn).toHaveBeenCalledTimes(1)
    })

    it('deve retornar o resultado da função assíncrona', async () => {
      const expectedResult = { data: 'test' }
      const asyncFn = vi.fn(() => Promise.resolve(expectedResult))

      const result = await loading.execute(asyncFn)

      expect(result).toEqual(expectedResult)
      expect(loading.loading.value).toBe(false)
    })

    it('deve capturar e definir erro quando a função falha', async () => {
      const errorMessage = 'Test error'
      const asyncFn = vi.fn(() => Promise.reject(new Error(errorMessage)))

      await expect(loading.execute(asyncFn)).rejects.toThrow(errorMessage)

      expect(loading.error.value).toBeInstanceOf(Error)
      expect(loading.error.value.message).toBe(errorMessage)
      expect(loading.loading.value).toBe(false)
    })

    it('deve limpar loading mesmo quando há erro', async () => {
      const asyncFn = vi.fn(() => Promise.reject(new Error('Error')))

      try {
        await loading.execute(asyncFn)
      } catch (e) {
        // Ignorar erro
      }

      expect(loading.loading.value).toBe(false)
    })

    it('deve limpar error antes de executar nova função', async () => {
      // Primeiro erro
      const errorFn = vi.fn(() => Promise.reject(new Error('First error')))
      try {
        await loading.execute(errorFn)
      } catch (e) {
        // Ignorar
      }

      expect(loading.error.value).toBeInstanceOf(Error)

      // Segunda execução bem-sucedida deve limpar o erro
      const successFn = vi.fn(() => Promise.resolve('success'))
      await loading.execute(successFn)

      expect(loading.error.value).toBeNull()
      expect(loading.loading.value).toBe(false)
    })
  })

  describe('setLoading', () => {
    it('deve definir loading manualmente', () => {
      loading.setLoading(true)
      expect(loading.loading.value).toBe(true)

      loading.setLoading(false)
      expect(loading.loading.value).toBe(false)
    })
  })

  describe('setError', () => {
    it('deve definir erro manualmente', () => {
      const error = new Error('Manual error')
      loading.setError(error)

      expect(loading.error.value).toBe(error)
    })

    it('deve aceitar null para limpar erro', () => {
      loading.setError(new Error('Error'))
      expect(loading.error.value).toBeInstanceOf(Error)

      loading.setError(null)
      expect(loading.error.value).toBeNull()
    })
  })

  describe('clearError', () => {
    it('deve limpar o erro', () => {
      loading.setError(new Error('Test error'))
      expect(loading.error.value).toBeInstanceOf(Error)

      loading.clearError()
      expect(loading.error.value).toBeNull()
    })
  })

  describe('reset', () => {
    it('deve resetar todos os estados', () => {
      // Configurar estados
      loading.setLoading(true)
      loading.setError(new Error('Test error'))

      expect(loading.loading.value).toBe(true)
      expect(loading.error.value).toBeInstanceOf(Error)

      // Resetar
      loading.reset()

      expect(loading.loading.value).toBe(false)
      expect(loading.error.value).toBeNull()
    })
  })

  describe('Integração', () => {
    it('deve funcionar corretamente em múltiplas execuções sequenciais', async () => {
      const fn1 = vi.fn(() => Promise.resolve('result1'))
      const fn2 = vi.fn(() => Promise.resolve('result2'))

      const result1 = await loading.execute(fn1)
      expect(result1).toBe('result1')
      expect(loading.loading.value).toBe(false)

      const result2 = await loading.execute(fn2)
      expect(result2).toBe('result2')
      expect(loading.loading.value).toBe(false)

      expect(fn1).toHaveBeenCalledTimes(1)
      expect(fn2).toHaveBeenCalledTimes(1)
    })

    it('deve manter estados independentes entre instâncias', () => {
      const loading1 = useLoading()
      const loading2 = useLoading()

      loading1.setLoading(true)
      loading2.setLoading(false)

      expect(loading1.loading.value).toBe(true)
      expect(loading2.loading.value).toBe(false)
    })
  })
})

