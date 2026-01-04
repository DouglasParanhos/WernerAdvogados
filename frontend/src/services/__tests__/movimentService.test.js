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

  describe('update', () => {
    it('deve chamar API PUT no endpoint correto com dados atualizados', async () => {
      const movimentId = 1
      const updatedMoviment = {
        descricao: 'Descrição atualizada',
        date: '2023-03-15T15:30:00.000Z',
        processId: 1,
        visibleToClient: true
      }
      const mockResponse = {
        data: {
          id: movimentId,
          ...updatedMoviment
        }
      }

      api.put.mockResolvedValue(mockResponse)

      await movimentService.update(movimentId, updatedMoviment)

      expect(api.put).toHaveBeenCalledWith(
        `/moviments/${movimentId}`,
        updatedMoviment
      )
    })

    it('deve retornar movimentação atualizada', async () => {
      const movimentId = 1
      const updatedMoviment = {
        descricao: 'Descrição atualizada',
        date: '2023-03-15T15:30:00.000Z',
        processId: 1,
        visibleToClient: true
      }
      const mockResponse = {
        data: {
          id: movimentId,
          ...updatedMoviment
        }
      }

      api.put.mockResolvedValue(mockResponse)

      const result = await movimentService.update(movimentId, updatedMoviment)

      expect(result).toEqual(mockResponse.data)
      expect(result.id).toBe(movimentId)
      expect(result.descricao).toBe('Descrição atualizada')
    })

    it('deve enviar payload completo incluindo id (remoção de id é responsabilidade do componente)', async () => {
      const movimentId = 1
      const movimentWithId = {
        id: 999, // ID pode estar presente, mas será ignorado pelo backend (usa o da URL)
        descricao: 'Descrição atualizada',
        date: '2023-03-15T15:30:00.000Z',
        processId: 1,
        visibleToClient: true
      }
      const mockResponse = {
        data: {
          id: movimentId,
          descricao: 'Descrição atualizada',
          date: '2023-03-15T15:30:00.000Z',
          processId: 1,
          visibleToClient: true
        }
      }

      api.put.mockResolvedValue(mockResponse)

      await movimentService.update(movimentId, movimentWithId)

      // Verifica que o serviço envia o payload como recebido
      const callArgs = api.put.mock.calls[0]
      expect(callArgs[0]).toBe(`/moviments/${movimentId}`)
      expect(callArgs[1]).toEqual(movimentWithId)
      expect(callArgs[1].descricao).toBe('Descrição atualizada')
    })

    it('deve aceitar formato ISO com timezone', async () => {
      const movimentId = 1
      const updatedMoviment = {
        descricao: 'Descrição atualizada',
        date: '2023-03-15T15:30:45.123Z', // Formato ISO com timezone
        processId: 1
      }
      const mockResponse = {
        data: {
          id: movimentId,
          ...updatedMoviment
        }
      }

      api.put.mockResolvedValue(mockResponse)

      const result = await movimentService.update(movimentId, updatedMoviment)

      expect(result).toEqual(mockResponse.data)
      expect(api.put).toHaveBeenCalledWith(
        `/moviments/${movimentId}`,
        updatedMoviment
      )
    })

    it('deve propagar erros da API', async () => {
      const movimentId = 1
      const updatedMoviment = {
        descricao: 'Descrição atualizada',
        date: '2023-03-15T15:30:00.000Z',
        processId: 1
      }
      const error = new Error('Erro ao atualizar movimentação')

      api.put.mockRejectedValue(error)

      await expect(movimentService.update(movimentId, updatedMoviment))
        .rejects.toThrow('Erro ao atualizar movimentação')
    })

    it('deve propagar erro 500 do servidor', async () => {
      const movimentId = 1
      const updatedMoviment = {
        descricao: 'Descrição atualizada',
        date: '2023-03-15T15:30:00.000Z',
        processId: 1
      }
      const error = {
        response: {
          status: 500,
          data: { message: 'I/O error while reading input message' }
        }
      }

      api.put.mockRejectedValue(error)

      await expect(movimentService.update(movimentId, updatedMoviment))
        .rejects.toEqual(error)
    })
  })
})

