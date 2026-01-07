/**
 * Centraliza o tratamento de erros da aplicação
 */

/**
 * Extrai mensagem de erro de uma resposta de erro do axios
 * @param {Error} error - Erro do axios
 * @returns {string} Mensagem de erro formatada
 */
export function getErrorMessage(error) {
  if (!error) {
    return 'Erro desconhecido'
  }

  // Erro de rede (sem resposta do servidor)
  if (!error.response) {
    if (error.request) {
      return 'Erro de conexão. Verifique sua internet e tente novamente.'
    }
    return error.message || 'Erro desconhecido'
  }

  const response = error.response
  const status = response.status
  const data = response.data

  // Mensagem específica do servidor
  if (data && data.message) {
    return data.message
  }

  // Mensagens padrão por código HTTP
  switch (status) {
    case 400:
      return 'Dados inválidos. Verifique os campos e tente novamente.'
    case 401:
      return 'Não autorizado. Faça login novamente.'
    case 403:
      return 'Acesso negado. Você não tem permissão para esta ação.'
    case 404:
      return 'Recurso não encontrado.'
    case 409:
      return 'Conflito. O recurso já existe ou está em uso.'
    case 422:
      return 'Erro de validação. Verifique os dados informados.'
    case 500:
      return 'Erro interno do servidor. Tente novamente mais tarde.'
    case 503:
      return 'Serviço temporariamente indisponível. Tente novamente mais tarde.'
    default:
      return `Erro ${status}: ${data?.message || 'Erro desconhecido'}`
  }
}

/**
 * Extrai erros de validação de uma resposta de erro
 * @param {Error} error - Erro do axios
 * @returns {Object} Objeto com erros por campo
 */
export function getValidationErrors(error) {
  if (!error || !error.response || !error.response.data) {
    return {}
  }

  const data = error.response.data

  // Erros de validação do Spring Boot (@Valid)
  if (data.errors && typeof data.errors === 'object') {
    return data.errors
  }

  // Mensagem única
  if (data.message) {
    return { _general: data.message }
  }

  return {}
}

/**
 * Verifica se o erro é de validação
 * @param {Error} error - Erro do axios
 * @returns {boolean}
 */
export function isValidationError(error) {
  return error?.response?.status === 400 || error?.response?.status === 422
}

/**
 * Verifica se o erro é de autenticação
 * @param {Error} error - Erro do axios
 * @returns {boolean}
 */
export function isAuthenticationError(error) {
  return error?.response?.status === 401
}

/**
 * Verifica se o erro é de autorização
 * @param {Error} error - Erro do axios
 * @returns {boolean}
 */
export function isAuthorizationError(error) {
  return error?.response?.status === 403
}

/**
 * Loga erro para debug (apenas em desenvolvimento)
 * @param {Error} error - Erro do axios
 * @param {string} context - Contexto onde o erro ocorreu
 */
export function logError(error, context = '') {
  if (import.meta.env.DEV) {
    console.error(`[ErrorHandler${context ? ` - ${context}` : ''}]`, {
      message: getErrorMessage(error),
      status: error?.response?.status,
      data: error?.response?.data,
      error
    })
  }
}

