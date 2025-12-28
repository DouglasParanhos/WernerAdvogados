import api from './api'

export const matriculationService = {
  async getAll(personId = null) {
    const params = personId ? { personId } : {}
    const response = await api.get('/matriculations', { params })
    return response.data
  },
  
  async getById(id) {
    const response = await api.get(`/matriculations/${id}`)
    return response.data
  },
  
  async create(matriculation) {
    const response = await api.post('/matriculations', matriculation)
    return response.data
  },
  
  async update(id, matriculation) {
    const response = await api.put(`/matriculations/${id}`, matriculation)
    return response.data
  },
  
  async delete(id) {
    await api.delete(`/matriculations/${id}`)
  }
}

