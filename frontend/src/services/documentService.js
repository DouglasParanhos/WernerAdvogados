import api from './api'

/**
 * Extrai o nome do arquivo do header Content-Disposition
 * @param {string} contentDisposition - Header Content-Disposition
 * @returns {string|null} - Nome do arquivo extraído ou null
 */
function extractFileName(contentDisposition) {
  if (!contentDisposition) return null
  
  // Tentar primeiro com filename entre aspas: filename="..."
  let match = contentDisposition.match(/filename="([^"]+)"/i)
  if (match && match[1]) {
    return match[1]
  }
  
  // Tentar com filename sem aspas: filename=...
  match = contentDisposition.match(/filename=([^;\s]+)/i)
  if (match && match[1]) {
    return match[1].trim()
  }
  
  return null
}

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
        const extractedFileName = extractFileName(contentDisposition)
        if (extractedFileName) {
          fileName = extractedFileName
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
   * Gera um documento PDF usando dados do cliente e faz download automático
   * Retorna PDF conforme implementação do backend
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
      // Verificar se é PDF pelo Content-Type
      const contentType = response.headers['content-type'] || ''
      const isPdf = contentType.includes('pdf') || contentType.includes('application/pdf')
      
      const blob = new Blob([response.data], { 
        type: isPdf ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
      })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      
      // Extrair nome do arquivo do header Content-Disposition ou usar templateName
      const contentDisposition = response.headers['content-disposition']
      let fileName = templateName.replace('.docx', '') + '_gerado.pdf'
      
      if (contentDisposition) {
        const extractedFileName = extractFileName(contentDisposition)
        if (extractedFileName) {
          fileName = extractedFileName
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
   * Retorna PDF para documentos editados de clientes
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
      // Verificar se é PDF pelo Content-Type
      const contentType = response.headers['content-type'] || ''
      const isPdf = contentType.includes('pdf') || contentType.includes('application/pdf')
      
      const blob = new Blob([response.data], { 
        type: isPdf ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
      })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      
      // Extrair nome do arquivo do header Content-Disposition
      const contentDisposition = response.headers['content-disposition']
      let fileName = templateName.replace('.docx', '') + '_customizado.pdf'
      
      if (contentDisposition) {
        const extractedFileName = extractFileName(contentDisposition)
        if (extractedFileName) {
          fileName = extractedFileName
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
   * Retorna PDF para documentos editados
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
      // Verificar se é PDF pelo Content-Type
      const contentType = response.headers['content-type'] || ''
      const isPdf = contentType.includes('pdf') || contentType.includes('application/pdf')
      
      const blob = new Blob([response.data], { 
        type: isPdf ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
      })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      
      // Extrair nome do arquivo do header Content-Disposition
      const contentDisposition = response.headers['content-disposition']
      let fileName = templateName.replace('.docx', '') + '_customizado.pdf'
      
      if (contentDisposition) {
        const extractedFileName = extractFileName(contentDisposition)
        if (extractedFileName) {
          fileName = extractedFileName
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

