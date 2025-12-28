import api from './api'

export const processService = {
  async getAll(personId = null) {
    const params = personId ? { personId } : {}
    const response = await api.get('/processes', { params })
    return response.data
  },
  
  async getById(id) {
    const response = await api.get(`/processes/${id}`)
    return response.data
  },
  
  async create(process) {
    const response = await api.post('/processes', process)
    return response.data
  },
  
  async update(id, process) {
    const response = await api.put(`/processes/${id}`, process)
    return response.data
  },
  
  async delete(id) {
    await api.delete(`/processes/${id}`)
  },
  
  async updateStatus(id, status) {
    const response = await api.patch(`/processes/${id}/status`, { status })
    return response.data
  },
  
  async getDistinctStatuses() {
    const response = await api.get('/processes/status/distinct')
    return response.data
  }
}

