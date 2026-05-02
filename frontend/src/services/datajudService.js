import api from './api'

export const datajudService = {
  /**
   * @param {string} dataInicio ISO date yyyy-MM-dd
   */
  async consultarMovimentos(dataInicio) {
    const response = await api.get('/datajud/movimentos', {
      params: { dataInicio }
    })
    return response.data
  }
}
