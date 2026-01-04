import api from './api'

export const backupService = {
  async createBackup() {
    const response = await api.post('/backup', {}, {
      responseType: 'blob'
    })
    return response.data
  }
}










