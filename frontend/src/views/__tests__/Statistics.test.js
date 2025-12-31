// Testes unitários para Statistics.vue
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import Statistics from '../Statistics.vue'
import { statisticsService } from '../../services/statisticsService'

// Mock do serviço
vi.mock('../../services/statisticsService', () => ({
  statisticsService: {
    getStatistics: vi.fn()
  }
}))

describe('Statistics.vue', () => {
  let wrapper

  const mockStatistics = {
    totalClients: 97,
    totalProcesses: 191,
    totalMatriculations: 100,
    totalMoviments: 50,
    processesByType: [
      { type: 'PISO', count: 79 },
      { type: 'NOVAESCOLA', count: 53 },
      { type: 'INTERNIVEIS', count: 59 }
    ],
    processesByComarca: [
      {
        comarca: 'Capital',
        byType: [
          { type: 'PISO', count: 30 },
          { type: 'NOVAESCOLA', count: 20 },
          { type: 'INTERNIVEIS', count: 14 }
        ]
      },
      {
        comarca: 'Niterói',
        byType: [
          { type: 'PISO', count: 25 },
          { type: 'NOVAESCOLA', count: 18 },
          { type: 'INTERNIVEIS', count: 15 }
        ]
      }
    ],
    processesByStatus: [
      {
        status: 'Ag. TJRJ Interniveis',
        byType: [
          { type: 'PISO', count: 10 },
          { type: 'NOVAESCOLA', count: 8 },
          { type: 'INTERNIVEIS', count: 11 }
        ]
      },
      {
        status: 'Suspenso PISO',
        byType: [
          { type: 'PISO', count: 22 },
          { type: 'NOVAESCOLA', count: 0 },
          { type: 'INTERNIVEIS', count: 0 }
        ]
      }
    ],
    honorariosByType: [
      {
        tipoProcesso: 'PISO',
        quantidadeProcessos: 79,
        totalHonorariosContratuais: 1613379.29,
        totalHonorariosSucumbenciais: 744513.85,
        totalHonorarios: 2357893.14
      }
    ]
  }

  beforeEach(() => {
    vi.clearAllMocks()
    statisticsService.getStatistics.mockResolvedValue(mockStatistics)
  })

  it('deve carregar estatísticas ao montar o componente', async () => {
    wrapper = mount(Statistics, {
      global: {
        mocks: {
          $router: {
            push: vi.fn()
          },
          $route: {
            name: 'Statistics'
          }
        }
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(statisticsService.getStatistics).toHaveBeenCalled()
    expect(wrapper.vm.statistics).toEqual(mockStatistics)
  })

  it('deve filtrar dados de comarca por tipo PISO corretamente', async () => {
    wrapper = mount(Statistics, {
      global: {
        mocks: {
          $router: { push: vi.fn() },
          $route: { name: 'Statistics' }
        }
      }
    })

    wrapper.vm.statistics = mockStatistics
    await wrapper.vm.$nextTick()

    const pisoData = wrapper.vm.getComarcaDataByType('PISO')
    expect(pisoData).toHaveLength(2)
    expect(pisoData[0].comarca).toBe('Capital')
    expect(pisoData[0].count).toBe(30)
    expect(pisoData[1].comarca).toBe('Niterói')
    expect(pisoData[1].count).toBe(25)
  })

  it('deve filtrar dados de status por tipo PISO corretamente', async () => {
    wrapper = mount(Statistics, {
      global: {
        mocks: {
          $router: { push: vi.fn() },
          $route: { name: 'Statistics' }
        }
      }
    })

    wrapper.vm.statistics = mockStatistics
    await wrapper.vm.$nextTick()

    const pisoData = wrapper.vm.getStatusDataByType('PISO')
    expect(pisoData).toHaveLength(2)
    expect(pisoData[0].status).toBe('Suspenso PISO')
    expect(pisoData[0].count).toBe(22)
    expect(pisoData[1].status).toBe('Ag. TJRJ Interniveis')
    expect(pisoData[1].count).toBe(10)
  })

  it('deve retornar array vazio quando não há dados', () => {
    wrapper = mount(Statistics, {
      global: {
        mocks: {
          $router: { push: vi.fn() },
          $route: { name: 'Statistics' }
        }
      }
    })

    wrapper.vm.statistics = null
    expect(wrapper.vm.getComarcaDataByType('PISO')).toEqual([])
    expect(wrapper.vm.getStatusDataByType('PISO')).toEqual([])
  })

  it('deve formatar moeda corretamente', () => {
    wrapper = mount(Statistics, {
      global: {
        mocks: {
          $router: { push: vi.fn() },
          $route: { name: 'Statistics' }
        }
      }
    })

    expect(wrapper.vm.formatCurrency(1000.50)).toBe('R$ 1.000,50')
    expect(wrapper.vm.formatCurrency(null)).toBe('R$ 0,00')
    expect(wrapper.vm.formatCurrency(0)).toBe('R$ 0,00')
  })

  it('deve retornar classe CSS correta para cada tipo', () => {
    wrapper = mount(Statistics, {
      global: {
        mocks: {
          $router: { push: vi.fn() },
          $route: { name: 'Statistics' }
        }
      }
    })

    expect(wrapper.vm.getTypeCardClass('PISO')).toBe('type-card-piso')
    expect(wrapper.vm.getTypeCardClass('NOVAESCOLA')).toBe('type-card-novaescola')
    expect(wrapper.vm.getTypeCardClass('INTERNIVEIS')).toBe('type-card-interniveis')
    expect(wrapper.vm.getTypeCardClass('piso')).toBe('type-card-piso') // case insensitive
  })

  it('deve retornar ícone correto para cada tipo', () => {
    wrapper = mount(Statistics, {
      global: {
        mocks: {
          $router: { push: vi.fn() },
          $route: { name: 'Statistics' }
        }
      }
    })

    expect(wrapper.vm.getTypeIcon('PISO')).toBe('🏢')
    expect(wrapper.vm.getTypeIcon('NOVAESCOLA')).toBe('🏫')
    expect(wrapper.vm.getTypeIcon('INTERNIVEIS')).toBe('📚')
    expect(wrapper.vm.getTypeIcon('UNKNOWN')).toBe('📋')
  })
})



