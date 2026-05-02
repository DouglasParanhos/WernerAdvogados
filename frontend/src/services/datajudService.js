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
  },

  /**
   * @param {number|string} processId
   * @param {string} dataInicio ISO date yyyy-MM-dd
   */
  async consultarMovimentosProcesso(processId, dataInicio) {
    const response = await api.get(`/datajud/movimentos/processo/${processId}`, {
      params: { dataInicio }
    })
    return response.data
  }
}
