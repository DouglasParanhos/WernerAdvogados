// Testes unitários para ClientLoginModal.vue
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import ClientLoginModal from '../ClientLoginModal.vue'
import { personService } from '../../services/personService'

// Mock do serviço
vi.mock('../../services/personService', () => ({
  personService: {
    getUsernameSuggestion: vi.fn(),
    configureCredentials: vi.fn()
  }
}))

describe('ClientLoginModal.vue', () => {
  let wrapper
  const mockClient = {
    id: 1,
    fullname: 'João Silva Santos'
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('deve renderizar o modal quando show é true', () => {
    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    expect(wrapper.find('.modal-overlay').exists()).toBe(true)
    expect(wrapper.find('.modal-content').exists()).toBe(true)
  })

  it('não deve renderizar o modal quando show é false', () => {
    wrapper = mount(ClientLoginModal, {
      props: {
        show: false,
        client: mockClient
      }
    })

    expect(wrapper.find('.modal-overlay').exists()).toBe(false)
  })

  it('deve carregar sugestão de username ao abrir o modal', async () => {
    personService.getUsernameSuggestion.mockResolvedValue({ username: 'joão.santos' })

    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(personService.getUsernameSuggestion).toHaveBeenCalledWith(1)
    expect(wrapper.vm.usernamePlaceholder).toBe('joão.santos')
  })

  it('deve gerar senha automaticamente ao abrir o modal', async () => {
    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    await wrapper.vm.$nextTick()

    expect(wrapper.vm.credentials.password).toBeTruthy()
    expect(wrapper.vm.credentials.password.length).toBe(8)
  })

  it('deve gerar senha com caracteres alfanuméricos e especiais', () => {
    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    wrapper.vm.generatePassword()
    const password = wrapper.vm.credentials.password

    expect(password.length).toBe(8)
    expect(password).toMatch(/[a-zA-Z0-9!@#$%^&*]{8}/)
  })

  it('deve permitir editar username e senha', async () => {
    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    await wrapper.vm.$nextTick()

    const usernameInput = wrapper.find('#username')
    const passwordInput = wrapper.find('#password')

    await usernameInput.setValue('novo.username')
    await passwordInput.setValue('NovaSenha123!')

    expect(wrapper.vm.credentials.username).toBe('novo.username')
    expect(wrapper.vm.credentials.password).toBe('NovaSenha123!')
  })

  it('deve salvar credenciais com sucesso', async () => {
    personService.getUsernameSuggestion.mockResolvedValue({ username: 'joão.santos' })
    personService.configureCredentials.mockResolvedValue({})

    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    wrapper.vm.credentials.username = 'joao.silva'
    wrapper.vm.credentials.password = 'Test123!'

    await wrapper.vm.save()

    expect(personService.configureCredentials).toHaveBeenCalledWith(1, {
      username: 'joao.silva',
      password: 'Test123!'
    })
    expect(wrapper.emitted('saved')).toBeTruthy()
  })

  it('deve validar campos obrigatórios antes de salvar', async () => {
    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    await wrapper.vm.$nextTick()

    wrapper.vm.credentials.username = ''
    wrapper.vm.credentials.password = ''

    await wrapper.vm.save()

    expect(wrapper.vm.error).toBe('Por favor, preencha todos os campos')
    expect(personService.configureCredentials).not.toHaveBeenCalled()
  })

  it('deve validar tamanho mínimo da senha', async () => {
    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    await wrapper.vm.$nextTick()

    wrapper.vm.credentials.username = 'joao.silva'
    wrapper.vm.credentials.password = 'Short1!'

    await wrapper.vm.save()

    expect(wrapper.vm.error).toBe('A senha deve ter no mínimo 8 caracteres')
    expect(personService.configureCredentials).not.toHaveBeenCalled()
  })

  it('deve exibir erro quando falhar ao salvar', async () => {
    personService.getUsernameSuggestion.mockResolvedValue({ username: 'joão.santos' })
    personService.configureCredentials.mockRejectedValue(new Error('Erro ao salvar'))

    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 100))

    wrapper.vm.credentials.username = 'joao.silva'
    wrapper.vm.credentials.password = 'Test123!'

    await wrapper.vm.save()

    expect(wrapper.vm.error).toContain('Erro ao salvar credenciais')
  })

  it('deve fechar o modal ao clicar em cancelar', async () => {
    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    await wrapper.vm.$nextTick()

    const cancelButton = wrapper.find('.btn-secondary')
    await cancelButton.trigger('click')

    expect(wrapper.emitted('close')).toBeTruthy()
  })

  it('deve resetar campos ao fechar o modal', async () => {
    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    await wrapper.vm.$nextTick()

    wrapper.vm.credentials.username = 'test'
    wrapper.vm.credentials.password = 'test123'
    wrapper.vm.error = 'Erro teste'

    wrapper.setProps({ show: false })
    await wrapper.vm.$nextTick()

    expect(wrapper.vm.credentials.username).toBe('')
    expect(wrapper.vm.credentials.password).toBe('')
    expect(wrapper.vm.error).toBeNull()
  })

  it('deve gerar nova senha ao clicar no botão gerar', async () => {
    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    await wrapper.vm.$nextTick()

    const firstPassword = wrapper.vm.credentials.password
    const generateButton = wrapper.find('.btn-generate')
    
    await generateButton.trigger('click')
    await wrapper.vm.$nextTick()

    const secondPassword = wrapper.vm.credentials.password
    expect(secondPassword).not.toBe(firstPassword)
    expect(secondPassword.length).toBe(8)
  })

  it('deve ter input de senha do tipo password por padrão', async () => {
    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    await wrapper.vm.$nextTick()

    const passwordInput = wrapper.find('#password')
    expect(passwordInput.attributes('type')).toBe('password')
  })

  it('deve alternar visibilidade da senha ao clicar no botão toggle', async () => {
    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    await wrapper.vm.$nextTick()

    const passwordInput = wrapper.find('#password')
    const toggleButton = wrapper.find('.password-toggle-btn')

    // Inicialmente deve ser password
    expect(passwordInput.attributes('type')).toBe('password')
    expect(wrapper.vm.showPassword).toBe(false)

    // Clicar para mostrar
    await toggleButton.trigger('click')
    await wrapper.vm.$nextTick()

    expect(passwordInput.attributes('type')).toBe('text')
    expect(wrapper.vm.showPassword).toBe(true)

    // Clicar novamente para ocultar
    await toggleButton.trigger('click')
    await wrapper.vm.$nextTick()

    expect(passwordInput.attributes('type')).toBe('password')
    expect(wrapper.vm.showPassword).toBe(false)
  })

  it('deve mostrar senha quando gerada automaticamente', async () => {
    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    await wrapper.vm.$nextTick()

    // Após gerar senha, showPassword deve ser true
    expect(wrapper.vm.showPassword).toBe(true)
    const passwordInput = wrapper.find('#password')
    expect(passwordInput.attributes('type')).toBe('text')
  })

  it('deve mostrar indicador de força quando senha atende requisitos', async () => {
    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    await wrapper.vm.$nextTick()

    // Senha gerada deve atender requisitos (tem minúsculas, maiúsculas, números e especiais)
    wrapper.vm.credentials.password = 'Test123!'
    await wrapper.vm.$nextTick()

    const strengthBar = wrapper.find('.password-strength-bar')
    expect(strengthBar.classes()).toContain('meets-requirements')
    expect(wrapper.vm.passwordMeetsRequirements).toBe(true)
  })

  it('não deve mostrar indicador de força quando senha não atende requisitos', async () => {
    wrapper = mount(ClientLoginModal, {
      props: {
        show: true,
        client: mockClient
      }
    })

    await wrapper.vm.$nextTick()

    // Senha sem maiúsculas
    wrapper.vm.credentials.password = 'test123!'
    await wrapper.vm.$nextTick()

    expect(wrapper.vm.passwordMeetsRequirements).toBe(false)

    // Senha sem números
    wrapper.vm.credentials.password = 'TestPass!'
    await wrapper.vm.$nextTick()

    expect(wrapper.vm.passwordMeetsRequirements).toBe(false)

    // Senha sem caracteres especiais
    wrapper.vm.credentials.password = 'Test1234'
    await wrapper.vm.$nextTick()

    expect(wrapper.vm.passwordMeetsRequirements).toBe(false)

    // Senha muito curta
    wrapper.vm.credentials.password = 'Test1!'
    await wrapper.vm.$nextTick()

    expect(wrapper.vm.passwordMeetsRequirements).toBe(false)
  })
})

