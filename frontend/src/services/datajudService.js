import api from './api'

export const datajudService = {
  /**
   * Inicia consulta em lote no servidor (202 + jobId); acompanhar com obterConsultaMovimentosJob.
   * @param {string} dataInicio ISO date yyyy-MM-dd
   * @returns {Promise<{ jobId: string, status: string }>}
   */
  async iniciarConsultaMovimentosAsync(dataInicio) {
    const response = await api.post(
      '/datajud/movimentos/async',
      null,
      { params: { dataInicio } }
    )
    return response.data
  },

  /**
   * @param {string} jobId
   * @returns {Promise<{ jobId: string, status: string, result?: object, errorMessage?: string }>}
   */
  async obterConsultaMovimentosJob(jobId) {
    const response = await api.get(`/datajud/movimentos/async/${jobId}`)
    return response.data
  },

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
