import api from './api'

export const calculationService = {
  /**
   * Gera cálculo NOVAESCOLA e faz download da planilha Excel
   */
  async calculateNovaEscola(grade, baseValue, ipcaEFactor, selicFactor) {
    try {
      const response = await api.post(
        '/calculations/novaescola',
        {
          grade: grade || null,
          baseValue: baseValue,
          ipcaEFactor: ipcaEFactor,
          selicFactor: selicFactor
        },
        {
          responseType: 'blob' // Importante para download de arquivo
        }
      )

      // Criar URL temporária para download
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      
      // Extrair nome do arquivo do header Content-Disposition ou usar nome padrão
      const contentDisposition = response.headers['content-disposition']
      let fileName = 'Calculo_NOVAESCOLA.xlsx'
      
      if (contentDisposition) {
        const fileNameMatch = contentDisposition.match(/filename="?(.+)"?/i)
        if (fileNameMatch && fileNameMatch[1]) {
          fileName = decodeURIComponent(fileNameMatch[1])
        }
      }
      
      link.setAttribute('download', fileName)
      document.body.appendChild(link)
      link.click()
      
      // Limpar
      link.remove()
      window.URL.revokeObjectURL(url)
      
      return { success: true, fileName }
    } catch (error) {
      console.error('Erro ao gerar cálculo:', error)
      throw error
    }
  },

  /**
   * Busca o fator de correção IPCA-E acumulado desde uma data
   */
  async getIpcaEFactor(dataInicio = '02/2003') {
    try {
      const response = await api.get('/calculations/factors/ipcae', {
        params: { dataInicio }
      })
      return response.data
    } catch (error) {
      console.error('Erro ao buscar fator IPCA-E:', error)
      throw error
    }
  },

  /**
   * Busca o fator de correção SELIC acumulado desde uma data
   */
  async getSelicFactor(dataInicio = '09/12/2021') {
    try {
      const response = await api.get('/calculations/factors/selic', {
        params: { dataInicio }
      })
      return response.data
    } catch (error) {
      console.error('Erro ao buscar fator SELIC:', error)
      throw error
    }
  }
}

