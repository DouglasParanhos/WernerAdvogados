// Testes unitários para movimentService.js
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { movimentService } from '../movimentService'
import api from '../api'

// Mock do api
vi.mock('../api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn()
  }
}))

describe('movimentService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getMyMoviments', () => {
    it('deve chamar API GET no endpoint correto', async () => {
      const mockMoviments = [
        {
          id: 1,
          descricao: 'Distribuição do processo',
          date: '2023-01-15T10:00:00',
          processId: 1,
          processNumero: '1234567-89.2023.8.19.0001',
          processComarca: 'Capital',
          processVara: '1ª Vara'
        }
      ]
      const mockResponse = { data: mockMoviments }

      api.get.mockResolvedValue(mockResponse)

      await movimentService.getMyMoviments()

      expect(api.get).toHaveBeenCalledWith('/moviments/my-moviments')
    })

    it('deve retornar lista de movimentações', async () => {
      const mockMoviments = [
        {
          id: 1,
          descricao: 'Distribuição do processo',
          date: '2023-01-15T10:00:00',
          processId: 1,
          processNumero: '1234567-89.2023.8.19.0001',
          processComarca: 'Capital',
          processVara: '1ª Vara'
        },
        {
          id: 2,
          descricao: 'Juntada de documentos',
          date: '2023-02-20T14:30:00',
          processId: 1,
          processNumero: '1234567-89.2023.8.19.0001',
          processComarca: 'Capital',
          processVara: '1ª Vara'
        }
      ]
      const mockResponse = { data: mockMoviments }

      api.get.mockResolvedValue(mockResponse)

      const result = await movimentService.getMyMoviments()

      expect(result).toEqual(mockMoviments)
      expect(result.length).toBe(2)
    })

    it('deve retornar array vazio quando não há movimentações', async () => {
      const mockResponse = { data: [] }

      api.get.mockResolvedValue(mockResponse)

      const result = await movimentService.getMyMoviments()

      expect(result).toEqual([])
    })

    it('deve propagar erros da API', async () => {
      const error = new Error('Erro na API')

      api.get.mockRejectedValue(error)

      await expect(movimentService.getMyMoviments()).rejects.toThrow('Erro na API')
    })

    it('deve propagar erro 403 de acesso negado', async () => {
      const error = {
        response: {
          status: 403,
          data: { message: 'Acesso negado' }
        }
      }

      api.get.mockRejectedValue(error)

      await expect(movimentService.getMyMoviments()).rejects.toEqual(error)
    })
  })
})

