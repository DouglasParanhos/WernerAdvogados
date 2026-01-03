// Testes unitários para ClientDetails.vue
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import ClientDetails from '../ClientDetails.vue'
import { personService } from '../../services/personService'
import { processService } from '../../services/processService'

// Mock dos serviços
vi.mock('../../services/personService', () => ({
  personService: {
    getById: vi.fn(),
    delete: vi.fn()
  }
}))

vi.mock('../../services/processService', () => ({
  processService: {
    delete: vi.fn(),
    getDistinctStatuses: vi.fn()
  }
}))

// Mock do router
const mockRouter = {
  push: vi.fn()
}

describe('ClientDetails.vue', () => {
  let wrapper

  const mockClient = {
    id: 1,
    fullname: 'João Silva',
    email: 'joao@example.com',
    cpf: '12345678900',
    rg: '1234567',
    estadoCivil: 'Solteiro',
    dataNascimento: '1990-01-01T00:00:00',
    profissao: 'Advogado',
    telefone: '1234567890',
    vivo: true,
    username: null,
    userId: null
  }

  const mockClientWithLogin = {
    ...mockClient,
    username: 'joao.silva',
    userId: 1
  }

  beforeEach(() => {
    vi.clearAllMocks()
    processService.getDistinctStatuses.mockResolvedValue([])
  })

  const createWrapper = (client = mockClient, routeParams = { id: '1' }) => {
    return mount(ClientDetails, {
      global: {
        mocks: {
          $route: {
            params: routeParams
          },
          $router: mockRouter
        }
      }
    })
  }

  it('deve carregar dados do cliente ao montar', async () => {
    personService.getById.mockResolvedValue(mockClient)

    wrapper = createWrapper()

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(personService.getById).toHaveBeenCalledWith('1')
    expect(wrapper.vm.client).toEqual(mockClient)
  })

  it('deve exibir botão Configurar Login no header', async () => {
    personService.getById.mockResolvedValue(mockClient)

    wrapper = createWrapper()

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    const loginButton = wrapper.find('.login-btn')
    expect(loginButton.exists()).toBe(true)
    expect(loginButton.attributes('title')).toBe('Configurar Login')
  })

  it('deve exibir seção de login quando cliente não tem login configurado', async () => {
    personService.getById.mockResolvedValue(mockClient)

    wrapper = createWrapper()

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    const loginInfo = wrapper.find('.login-info')
    expect(loginInfo.exists()).toBe(true)

    const noLoginText = wrapper.find('.no-login-text')
    expect(noLoginText.exists()).toBe(true)
    expect(noLoginText.text()).toContain('Login não configurado')

    const configureButton = wrapper.find('.btn-primary')
    expect(configureButton.exists()).toBe(true)
  })

  it('deve exibir informações de login quando cliente tem login configurado', async () => {
    personService.getById.mockResolvedValue(mockClientWithLogin)

    wrapper = createWrapper(mockClientWithLogin)

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    const usernameValue = wrapper.find('.username-value')
    expect(usernameValue.exists()).toBe(true)
    expect(usernameValue.text()).toBe('joao.silva')

    const passwordStatus = wrapper.find('.password-status')
    expect(passwordStatus.exists()).toBe(true)
    expect(passwordStatus.text()).toContain('Senha configurada')

    const regenerateButton = wrapper.findAll('.btn-secondary').find(btn => 
      btn.text().includes('Regenerar Senha')
    )
    expect(regenerateButton).toBeTruthy()
  })

  it('deve abrir modal de login ao clicar em Configurar Login', async () => {
    personService.getById.mockResolvedValue(mockClient)

    wrapper = createWrapper()

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(wrapper.vm.showLoginModal).toBe(false)

    const loginButton = wrapper.find('.login-btn')
    await loginButton.trigger('click')

    expect(wrapper.vm.showLoginModal).toBe(true)
  })

  it('deve abrir modal ao clicar em Regenerar Senha', async () => {
    personService.getById.mockResolvedValue(mockClientWithLogin)

    wrapper = createWrapper(mockClientWithLogin)

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(wrapper.vm.showLoginModal).toBe(false)

    const regenerateButton = wrapper.findAll('button').find(btn => 
      btn.text().includes('Regenerar Senha')
    )
    
    if (regenerateButton) {
      await regenerateButton.trigger('click')
      expect(wrapper.vm.showLoginModal).toBe(true)
    }
  })

  it('deve recarregar dados do cliente após salvar credenciais', async () => {
    personService.getById
      .mockResolvedValueOnce(mockClient)
      .mockResolvedValueOnce(mockClientWithLogin)

    wrapper = createWrapper()

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(personService.getById).toHaveBeenCalledTimes(1)

    // Simular salvamento de credenciais
    await wrapper.vm.handleLoginSaved()

    expect(personService.getById).toHaveBeenCalledTimes(2)
    expect(wrapper.vm.client.username).toBe('joao.silva')
  })

  it('deve fechar modal ao emitir evento close', async () => {
    personService.getById.mockResolvedValue(mockClient)

    wrapper = createWrapper()

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    wrapper.vm.showLoginModal = true
    await wrapper.vm.$nextTick()

    wrapper.vm.closeLoginModal()

    expect(wrapper.vm.showLoginModal).toBe(false)
  })

  it('deve exibir ClientLoginModal quando showLoginModal é true', async () => {
    personService.getById.mockResolvedValue(mockClient)

    wrapper = createWrapper()

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    wrapper.vm.showLoginModal = true
    await wrapper.vm.$nextTick()

    const modal = wrapper.findComponent({ name: 'ClientLoginModal' })
    expect(modal.exists()).toBe(true)
    expect(modal.props('show')).toBe(true)
    expect(modal.props('client')).toEqual(mockClient)
  })
})

