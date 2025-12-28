import api from './api'

export const movimentService = {
  async getAll(processId = null) {
    const params = processId ? { processId } : {}
    const response = await api.get('/moviments', { params })
    return response.data
  },
  
  async getById(id) {
    const response = await api.get(`/moviments/${id}`)
    return response.data
  },
  
  async create(moviment) {
    const response = await api.post('/moviments', moviment)
    return response.data
  },
  
  async update(id, moviment) {
    const response = await api.put(`/moviments/${id}`, moviment)
    return response.data
  },
  
  async delete(id) {
    await api.delete(`/moviments/${id}`)
  }
}

