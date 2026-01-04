// Testes unitários para personService.js
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { personService } from '../personService'
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

describe('personService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('configureCredentials', () => {
    it('deve chamar API POST com dados corretos', async () => {
      const personId = 1
      const credentials = {
        username: 'joao.silva',
        password: 'Test123!'
      }
      const mockResponse = { data: {} }

      api.post.mockResolvedValue(mockResponse)

      await personService.configureCredentials(personId, credentials)

      expect(api.post).toHaveBeenCalledWith(
        `/persons/${personId}/credentials`,
        credentials
      )
    })

    it('deve retornar dados da resposta', async () => {
      const personId = 1
      const credentials = {
        username: 'joao.silva',
        password: 'Test123!'
      }
      const mockResponse = { data: { success: true } }

      api.post.mockResolvedValue(mockResponse)

      const result = await personService.configureCredentials(personId, credentials)

      expect(result).toEqual({ success: true })
    })

    it('deve propagar erros da API', async () => {
      const personId = 1
      const credentials = {
        username: 'joao.silva',
        password: 'Test123!'
      }
      const error = new Error('Erro na API')

      api.post.mockRejectedValue(error)

      await expect(
        personService.configureCredentials(personId, credentials)
      ).rejects.toThrow('Erro na API')
    })
  })

  describe('getUsernameSuggestion', () => {
    it('deve chamar API GET com ID correto', async () => {
      const personId = 1
      const mockResponse = {
        data: { username: 'joão.santos' }
      }

      api.get.mockResolvedValue(mockResponse)

      await personService.getUsernameSuggestion(personId)

      expect(api.get).toHaveBeenCalledWith(
        `/persons/${personId}/username-suggestion`
      )
    })

    it('deve retornar sugestão de username', async () => {
      const personId = 1
      const mockResponse = {
        data: { username: 'joão.santos' }
      }

      api.get.mockResolvedValue(mockResponse)

      const result = await personService.getUsernameSuggestion(personId)

      expect(result).toEqual({ username: 'joão.santos' })
    })

    it('deve propagar erros da API', async () => {
      const personId = 1
      const error = new Error('Erro na API')

      api.get.mockRejectedValue(error)

      await expect(
        personService.getUsernameSuggestion(personId)
      ).rejects.toThrow('Erro na API')
    })
  })
})

