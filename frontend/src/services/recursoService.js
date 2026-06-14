import api from './api'

export const recursoService = {
  async getByProcessId(processId) {
    const response = await api.get('/recursos', { params: { processId } })
    return response.data
  },

  async getById(id) {
    const response = await api.get(`/recursos/${id}`)
    return response.data
  },

  async create(recurso) {
    const response = await api.post('/recursos', recurso)
    return response.data
  },

  async update(id, recurso) {
    const response = await api.put(`/recursos/${id}`, recurso)
    return response.data
  },

  async baixar(id) {
    const response = await api.patch(`/recursos/${id}/baixar`)
    return response.data
  },

  async delete(id) {
    await api.delete(`/recursos/${id}`)
  }
}
