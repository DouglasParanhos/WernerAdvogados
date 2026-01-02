import api from './api'
import { authService } from './authService'

export const statisticsService = {
  async getStatistics() {
    const response = await api.get('/statistics')
    return response.data
  },
  async updateProcessValues() {
    const response = await api.post('/statistics/update-process-values')
    return response.data
  },
  async getProcessStatus() {
    const response = await api.get('/statistics/process-status')
    return response.data
  },
  connectToStatusStream(onMessage, onError) {
    const token = authService.getToken()
    let baseURL = import.meta.env.VITE_API_URL || '/api'
    
    // Se baseURL é relativo, construir URL absoluta
    if (baseURL.startsWith('/')) {
      // Em desenvolvimento, usar localhost:8081
      if (import.meta.env.DEV) {
        baseURL = `http://localhost:8081${baseURL}`
      }
      // Em produção, usar o mesmo host
      else {
        baseURL = `${window.location.origin}${baseURL}`
      }
    }
    
    const url = `${baseURL}/statistics/process-status/stream${token ? `?token=${encodeURIComponent(token)}` : ''}`
    
    const eventSource = new EventSource(url)
    
    eventSource.addEventListener('status', (event) => {
      try {
        const data = JSON.parse(event.data)
        onMessage(data)
      } catch (error) {
        console.error('Erro ao parsear dados do SSE:', error)
      }
    })
    
    eventSource.onerror = (error) => {
      console.error('Erro na conexão SSE:', error)
      if (onError) {
        onError(error)
      }
      eventSource.close()
    }
    
    return eventSource
  }
}









