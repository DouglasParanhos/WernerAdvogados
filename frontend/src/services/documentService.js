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
  },

  /**
   * Obtém o conteúdo de um documento do cliente para edição
   */
  async getClientDocumentContent(personId, templateName) {
    const response = await api.get('/documents/client-content', {
      params: { personId, templateName }
    })
    return response.data
  },

  /**
   * Gera um documento customizado a partir de conteúdo editado (Quill Delta)
   */
  async generateCustomClientDocument(personId, templateName, content) {
    try {
      const response = await api.post(
        '/documents/generate-client-custom',
        {
          personId,
          templateName,
          content
        },
        {
          responseType: 'blob'
        }
      )

      // Criar URL temporária para download
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      
      // Extrair nome do arquivo do header Content-Disposition
      const contentDisposition = response.headers['content-disposition']
      let fileName = templateName.replace('.docx', '') + '_customizado.docx'
      
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
      console.error('Erro ao gerar documento customizado:', error)
      throw error
    }
  },

  /**
   * Obtém o conteúdo de um documento de processo para edição
   */
  async getProcessDocumentContent(processId, templateName) {
    const response = await api.get('/documents/process-content', {
      params: { processId, templateName }
    })
    return response.data
  },

  /**
   * Gera um documento customizado a partir de conteúdo editado (Quill Delta) para processo
   */
  async generateCustomProcessDocument(processId, templateName, content) {
    try {
      const response = await api.post(
        '/documents/generate-process-custom',
        {
          processId,
          templateName,
          content
        },
        {
          responseType: 'blob'
        }
      )

      // Criar URL temporária para download
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      
      // Extrair nome do arquivo do header Content-Disposition
      const contentDisposition = response.headers['content-disposition']
      let fileName = templateName.replace('.docx', '') + '_customizado.docx'
      
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
      console.error('Erro ao gerar documento customizado do processo:', error)
      throw error
    }
  }
}

