import { ref, watch, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authService } from '../services/authService'

/**
 * Composable para gerenciar estado de autenticação
 * @param {Object} options - Opções de configuração
 * @param {boolean} options.watchRoute - Se deve observar mudanças de rota (padrão: true)
 * @param {boolean} options.watchStorage - Se deve observar mudanças no localStorage (padrão: false)
 * @returns {Object} Objeto com estado de autenticação e métodos relacionados
 */
export function useAuth(options = {}) {
  const { watchRoute = true, watchStorage = false } = options
  const route = useRoute()
  const router = useRouter()
  
  const user = ref(authService.getUser())
  const isAuthenticated = ref(authService.isAuthenticated())

  /**
   * Atualiza o estado de autenticação
   */
  const updateAuthState = () => {
    isAuthenticated.value = authService.isAuthenticated()
    user.value = authService.getUser()
  }

  /**
   * Faz logout do usuário
   */
  const logout = () => {
    authService.logout()
    updateAuthState()
    router.push('/')
  }

  /**
   * Retorna a rota inicial baseada no papel do usuário
   */
  const getHomeRoute = () => {
    if (!isAuthenticated.value) {
      return '/'
    }
    // Se for cliente, redirecionar para página de movimentos
    if (user.value && user.value.role === 'CLIENT') {
      return '/my-moviments'
    }
    // Caso contrário, ir para dashboard
    return '/dashboard'
  }

  /**
   * Observa mudanças na rota para atualizar estado de autenticação
   */
  let routeWatcher = null
  if (watchRoute) {
    routeWatcher = watch(() => route.path, () => {
      updateAuthState()
    })
  }

  /**
   * Observa mudanças no localStorage para atualizar estado quando login/logout ocorrer em outra aba
   */
  if (watchStorage && typeof window !== 'undefined') {
    window.addEventListener('storage', updateAuthState)
    
    // Limpar listener quando o composable não for mais usado
    onBeforeUnmount(() => {
      window.removeEventListener('storage', updateAuthState)
    })
  }

  return {
    user,
    isAuthenticated,
    updateAuthState,
    logout,
    getHomeRoute
  }
}

