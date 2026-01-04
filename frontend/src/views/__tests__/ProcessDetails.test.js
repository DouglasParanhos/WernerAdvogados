// Testes unitários para ProcessDetails.vue
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import ProcessDetails from '../ProcessDetails.vue'
import { processService } from '../../services/processService'
import { movimentService } from '../../services/movimentService'
import { matriculationService } from '../../services/matriculationService'

// Mock dos serviços
vi.mock('../../services/processService', () => ({
  processService: {
    getById: vi.fn()
  }
}))

vi.mock('../../services/movimentService', () => ({
  movimentService: {
    getAll: vi.fn(),
    update: vi.fn(),
    delete: vi.fn()
  }
}))

vi.mock('../../services/matriculationService', () => ({
  matriculationService: {
    getById: vi.fn()
  }
}))

// Mock do router
const mockRouter = {
  push: vi.fn()
}

const mockRoute = {
  params: {
    id: '1'
  }
}

describe('ProcessDetails.vue - saveEditMoviment', () => {
  let wrapper

  const mockProcess = {
    id: 1,
    numero: '1234567-89.2023.8.19.0001',
    comarca: 'Capital',
    vara: '1ª Vara',
    tipoProcesso: 'Cível',
    status: 'Em andamento'
  }

  const mockMoviment = {
    id: 1,
    descricao: 'Distribuição do processo',
    date: '2023-01-15T10:00:00',
    processId: 1,
    visibleToClient: true
  }

  beforeEach(() => {
    vi.clearAllMocks()
    
    // Mock dos serviços
    processService.getById.mockResolvedValue(mockProcess)
    movimentService.getAll.mockResolvedValue([mockMoviment])
    
    wrapper = mount(ProcessDetails, {
      global: {
        mocks: {
          $router: mockRouter,
          $route: mockRoute
        }
      }
    })

    // Setup inicial após montagem
    wrapper.setData({
      process: mockProcess,
      moviments: [mockMoviment],
      editingMovimentId: 1,
      editingMoviment: {
        id: 1,
        descricao: 'Distribuição do processo',
        date: '2023-01-15T10:00',
        processId: 1,
        visibleToClient: true
      },
      loading: false
    })
  })

  it('deve atualizar movimentação com sucesso', async () => {
    // Arrange
    const updatedMoviment = {
      id: 1,
      descricao: 'Descrição atualizada',
      date: '2023-03-15T15:30:00.000Z',
      processId: 1,
      visibleToClient: false
    }

    wrapper.setData({
      editingMoviment: {
        id: 1,
        descricao: 'Descrição atualizada',
        date: '2023-03-15T15:30',
        processId: 1,
        visibleToClient: false
      }
    })

    movimentService.update.mockResolvedValue(updatedMoviment)
    // loadMoviments chama getAll com processId do $route.params.id (string '1')
    movimentService.getAll.mockResolvedValue([updatedMoviment])

    // Act
    await wrapper.vm.saveEditMoviment()
    await wrapper.vm.$nextTick()

    // Assert
    expect(movimentService.update).toHaveBeenCalledWith(1, {
      descricao: 'Descrição atualizada',
      date: expect.stringMatching(/2023-03-15T\d{2}:\d{2}:\d{2}/),
      processId: 1,
      visibleToClient: false
    })
    expect(movimentService.update.mock.calls[0][1]).not.toHaveProperty('id')
    // loadMoviments chama getAll com o processId do route (string '1')
    expect(movimentService.getAll).toHaveBeenCalledWith('1')
  })

  it('deve remover campo id do payload antes de enviar', async () => {
    // Arrange
    wrapper.setData({
      editingMoviment: {
        id: 1,
        descricao: 'Descrição atualizada',
        date: '2023-03-15T15:30',
        processId: 1,
        visibleToClient: true
      }
    })

    movimentService.update.mockResolvedValue({ id: 1, ...wrapper.vm.editingMoviment })
    movimentService.getAll.mockResolvedValue([])

    // Act
    await wrapper.vm.saveEditMoviment()
    await wrapper.vm.$nextTick()

    // Assert
    const updateCall = movimentService.update.mock.calls[0]
    expect(updateCall[0]).toBe(1) // ID da URL
    expect(updateCall[1]).not.toHaveProperty('id') // ID não deve estar no body
    expect(updateCall[1].descricao).toBe('Descrição atualizada')
  })

  it('deve converter data para formato ISO antes de enviar', async () => {
    // Arrange
    wrapper.setData({
      editingMoviment: {
        id: 1,
        descricao: 'Descrição atualizada',
        date: '2023-03-15T15:30', // Formato datetime-local
        processId: 1,
        visibleToClient: true
      }
    })

    movimentService.update.mockResolvedValue({ id: 1 })
    movimentService.getAll.mockResolvedValue([])

    // Act
    await wrapper.vm.saveEditMoviment()
    await wrapper.vm.$nextTick()

    // Assert
    const updateCall = movimentService.update.mock.calls[0]
    const sentDate = updateCall[1].date
    expect(sentDate).toMatch(/2023-03-15T\d{2}:\d{2}:\d{2}/) // Formato ISO
  })

  it('deve exibir alert quando descrição está vazia', async () => {
    // Arrange
    const alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {})
    wrapper.setData({
      editingMoviment: {
        id: 1,
        descricao: '',
        date: '2023-03-15T15:30',
        processId: 1
      }
    })

    // Act
    await wrapper.vm.saveEditMoviment()

    // Assert
    expect(alertSpy).toHaveBeenCalledWith('Por favor, preencha todos os campos')
    expect(movimentService.update).not.toHaveBeenCalled()
    
    alertSpy.mockRestore()
  })

  it('deve exibir alert quando data está vazia', async () => {
    // Arrange
    const alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {})
    wrapper.setData({
      editingMoviment: {
        id: 1,
        descricao: 'Descrição atualizada',
        date: '',
        processId: 1
      }
    })

    // Act
    await wrapper.vm.saveEditMoviment()

    // Assert
    expect(alertSpy).toHaveBeenCalledWith('Por favor, preencha todos os campos')
    expect(movimentService.update).not.toHaveBeenCalled()
    
    alertSpy.mockRestore()
  })

  it('deve exibir alert quando ocorre erro ao salvar', async () => {
    // Arrange
    const alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {})
    const error = {
      response: {
        data: {
          message: 'Erro ao salvar movimentação'
        }
      }
    }

    wrapper.setData({
      editingMoviment: {
        id: 1,
        descricao: 'Descrição atualizada',
        date: '2023-03-15T15:30',
        processId: 1,
        visibleToClient: true
      }
    })

    movimentService.update.mockRejectedValue(error)

    // Act
    await wrapper.vm.saveEditMoviment()

    // Assert
    expect(alertSpy).toHaveBeenCalledWith('Erro ao salvar movimentação: Erro ao salvar movimentação')
    
    alertSpy.mockRestore()
  })

  it('deve recarregar movimentações após atualização bem-sucedida', async () => {
    // Arrange
    const updatedMoviments = [
      {
        id: 1,
        descricao: 'Descrição atualizada',
        date: '2023-03-15T15:30:00',
        processId: 1
      }
    ]

    wrapper.setData({
      editingMoviment: {
        id: 1,
        descricao: 'Descrição atualizada',
        date: '2023-03-15T15:30',
        processId: 1,
        visibleToClient: true
      }
    })

    movimentService.update.mockResolvedValue({ id: 1 })
    movimentService.getAll.mockResolvedValue(updatedMoviments)

    // Act
    await wrapper.vm.saveEditMoviment()
    await wrapper.vm.$nextTick()

    // Assert
    // loadMoviments chama getAll com o processId do route (string '1')
    expect(movimentService.getAll).toHaveBeenCalledWith('1')
    expect(wrapper.vm.editingMovimentId).toBeNull()
    expect(wrapper.vm.editingMoviment).toBeNull()
  })

  it('deve cancelar edição após atualização bem-sucedida', async () => {
    // Arrange
    wrapper.setData({
      editingMoviment: {
        id: 1,
        descricao: 'Descrição atualizada',
        date: '2023-03-15T15:30',
        processId: 1,
        visibleToClient: true
      },
      editingMovimentId: 1
    })

    movimentService.update.mockResolvedValue({ id: 1 })
    movimentService.getAll.mockResolvedValue([])

    // Act
    await wrapper.vm.saveEditMoviment()
    await wrapper.vm.$nextTick()

    // Assert
    expect(wrapper.vm.editingMovimentId).toBeNull()
    expect(wrapper.vm.editingMoviment).toBeNull()
  })
})

