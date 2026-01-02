import api from './api'

export const processService = {
  async getAll(personId = null, page = null, size = null, filters = {}) {
    const params = {}
    if (personId) params.personId = personId
    if (page !== null) params.page = page
    if (size !== null) params.size = size
    if (filters.numero) params.numero = filters.numero
    if (filters.comarca) params.comarca = filters.comarca
    if (filters.vara) params.vara = filters.vara
    if (filters.tipoProcesso) params.tipoProcesso = filters.tipoProcesso
    if (filters.status) params.status = filters.status
    if (filters.showArchived !== undefined) params.showArchived = filters.showArchived
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

