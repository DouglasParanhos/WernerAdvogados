import api from './api'

export const personService = {
  async getAll(page = null, size = null, search = null) {
    const params = {}
    if (page !== null) params.page = page
    if (size !== null) params.size = size
    if (search !== null && search.trim() !== '') params.search = search
    const response = await api.get('/persons', { params })
    return response.data
  },
  
  async getById(id) {
    const response = await api.get(`/persons/${id}`)
    return response.data
  },
  
  async create(person) {
    const response = await api.post('/persons', person)
    return response.data
  },
  
  async update(id, person) {
    const response = await api.put(`/persons/${id}`, person)
    return response.data
  },
  
  async delete(id) {
    await api.delete(`/persons/${id}`)
  }
}

