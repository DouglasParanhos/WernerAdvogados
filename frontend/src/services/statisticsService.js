import api from './api'

export const statisticsService = {
  async getStatistics() {
    const response = await api.get('/statistics')
    return response.data
  }
}








