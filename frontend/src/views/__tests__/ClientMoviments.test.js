// Testes unitários para ClientMoviments.vue
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import ClientMoviments from '../ClientMoviments.vue'
import { movimentService } from '../../services/movimentService'

// Mock do serviço
vi.mock('../../services/movimentService', () => ({
  movimentService: {
    getMyMoviments: vi.fn()
  }
}))

describe('ClientMoviments.vue', () => {
  let wrapper

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

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('deve carregar movimentações ao montar o componente', async () => {
    movimentService.getMyMoviments.mockResolvedValue(mockMoviments)

    wrapper = mount(ClientMoviments)

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(movimentService.getMyMoviments).toHaveBeenCalled()
    expect(wrapper.vm.moviments).toEqual(mockMoviments)
  })

  it('deve exibir lista de movimentações quando há dados', async () => {
    movimentService.getMyMoviments.mockResolvedValue(mockMoviments)

    wrapper = mount(ClientMoviments)

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(wrapper.find('.moviments-list').exists()).toBe(true)
    expect(wrapper.findAll('.moviment-card').length).toBe(2)
  })

  it('deve exibir mensagem quando não há movimentações', async () => {
    movimentService.getMyMoviments.mockResolvedValue([])

    wrapper = mount(ClientMoviments)

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(wrapper.find('.empty-state').exists()).toBe(true)
    expect(wrapper.find('.moviments-list').exists()).toBe(false)
  })

  it('deve exibir informações corretas de cada movimentação', async () => {
    movimentService.getMyMoviments.mockResolvedValue(mockMoviments)

    wrapper = mount(ClientMoviments)

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    const cards = wrapper.findAll('.moviment-card')
    expect(cards[0].text()).toContain('Distribuição do processo')
    expect(cards[0].text()).toContain('1234567-89.2023.8.19.0001')
    expect(cards[0].text()).toContain('Capital')
    expect(cards[0].text()).toContain('1ª Vara')
  })

  it('deve formatar data corretamente', () => {
    wrapper = mount(ClientMoviments)

    const formattedDate = wrapper.vm.formatDate('2023-01-15T10:00:00')
    expect(formattedDate).toContain('15/01/2023')
    expect(formattedDate).toContain('10:00')
  })

  it('deve retornar "-" quando data é null', () => {
    wrapper = mount(ClientMoviments)

    const formattedDate = wrapper.vm.formatDate(null)
    expect(formattedDate).toBe('-')
  })

  it('deve exibir erro quando falhar ao carregar movimentações', async () => {
    const errorMessage = 'Erro ao carregar movimentações'
    movimentService.getMyMoviments.mockRejectedValue(new Error(errorMessage))

    wrapper = mount(ClientMoviments)

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(wrapper.vm.error).toContain(errorMessage)
    expect(wrapper.find('.error').exists()).toBe(true)
  })

  it('deve exibir erro específico para acesso negado', async () => {
    const error = {
      response: {
        status: 403,
        data: { message: 'Acesso negado' }
      }
    }
    movimentService.getMyMoviments.mockRejectedValue(error)

    wrapper = mount(ClientMoviments)

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(wrapper.vm.error).toContain('Acesso negado')
    expect(wrapper.vm.error).toContain('Apenas clientes podem visualizar')
  })

  it('deve exibir estado de loading durante carregamento', () => {
    movimentService.getMyMoviments.mockImplementation(() => new Promise(() => {}))

    wrapper = mount(ClientMoviments)

    expect(wrapper.vm.loading).toBe(true)
    expect(wrapper.find('.loading').exists()).toBe(true)
  })

  it('deve ocultar loading após carregar dados', async () => {
    movimentService.getMyMoviments.mockResolvedValue(mockMoviments)

    wrapper = mount(ClientMoviments)

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(wrapper.vm.loading).toBe(false)
    expect(wrapper.find('.loading').exists()).toBe(false)
  })
})

