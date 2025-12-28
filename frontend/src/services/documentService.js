import api from './api'

export const documentService = {
  /**
   * Busca templates disponíveis para um processo
   */
  async getTemplates(processId) {
    const response = await api.get('/documents/templates', {
      params: { processId }
    })
    return response.data
  },

  /**
   * Gera um documento e faz download automático
   */
  async generateDocument(processId, templateName) {
    try {
      const response = await api.post(
        '/documents/generate',
        {
          processId,
          templateName
        },
        {
          responseType: 'blob' // Importante para download de arquivo
        }
      )

      // Criar URL temporária para download
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      
      // Extrair nome do arquivo do header Content-Disposition ou usar templateName
      const contentDisposition = response.headers['content-disposition']
      let fileName = templateName.replace('.docx', '') + '_gerado.docx'
      
      if (contentDisposition) {
        const fileNameMatch = contentDisposition.match(/filename="?(.+)"?/i)
        if (fileNameMatch && fileNameMatch[1]) {
          fileName = fileNameMatch[1]
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
      console.error('Erro ao gerar documento:', error)
      throw error
    }
  },

  /**
   * Busca templates disponíveis para um cliente
   */
  async getClientTemplates(personId) {
    const response = await api.get('/documents/client-templates', {
      params: { personId }
    })
    return response.data
  },

  /**
   * Gera um documento usando dados do cliente e faz download automático
   */
  async generateClientDocument(personId, templateName) {
    try {
      const response = await api.post(
        '/documents/generate-client',
        {
          personId,
          templateName
        },
        {
          responseType: 'blob' // Importante para download de arquivo
        }
      )

      // Criar URL temporária para download
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      
      // Extrair nome do arquivo do header Content-Disposition ou usar templateName
      const contentDisposition = response.headers['content-disposition']
      let fileName = templateName.replace('.docx', '') + '_gerado.docx'
      
      if (contentDisposition) {
        const fileNameMatch = contentDisposition.match(/filename="?(.+)"?/i)
        if (fileNameMatch && fileNameMatch[1]) {
          fileName = fileNameMatch[1]
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
      console.error('Erro ao gerar documento do cliente:', error)
      throw error
    }
  }
}

