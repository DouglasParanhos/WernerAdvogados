import axios from 'axios'
import router from '../router'
import { authService } from './authService'
import { getErrorMessage, logError, isAuthenticationError } from '../utils/errorHandler'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api/v1',
  headers: {
    'Content-Type': 'application/json'
  }
})

// Validar variável de ambiente obrigatória em produção
if (import.meta.env.PROD && !import.meta.env.VITE_API_URL) {
  console.warn('VITE_API_URL não está definida. Usando URL relativa.')
}

// Interceptor para adicionar token em todas as requisições
api.interceptors.request.use(
  config => {
    const token = authService.getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    logError(error, 'Request Interceptor')
    return Promise.reject(error)
  }
)

// Interceptor para tratar erros de resposta
api.interceptors.response.use(
  response => response,
  error => {
    // Log do erro para debug
    logError(error, 'Response Interceptor')
    
    // Tratar erro de autenticação (401)
    if (isAuthenticationError(error)) {
      // Só redirecionar para login se não estiver em uma rota pública
      const currentRoute = router.currentRoute.value
      const isPublicRoute = currentRoute.path === '/' || 
                           currentRoute.name === 'HomePublic' ||
                           currentRoute.name === 'About' ||
                           currentRoute.name === 'Areas' ||
                           currentRoute.name === 'Privacy' ||
                           currentRoute.name === 'Interniveis' ||
                           currentRoute.path === '/login'
      
      authService.logout()
      
      // Só redirecionar se não estiver em uma rota pública
      if (!isPublicRoute) {
        router.push('/login')
      }
    }
    
    // Adicionar mensagem de erro formatada ao objeto de erro
    if (error.response) {
      error.userMessage = getErrorMessage(error)
    }
    
    return Promise.reject(error)
  }
)

export default api

