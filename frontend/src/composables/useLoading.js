import { ref } from 'vue'

/**
 * Composable para gerenciar estados de loading
 * @returns {Object} Objeto com estado de loading e métodos para controlá-lo
 */
export function useLoading() {
  const loading = ref(false)
  const error = ref(null)

  /**
   * Executa uma função assíncrona com controle de loading e erro
   * @param {Function} asyncFn - Função assíncrona a ser executada
   * @returns {Promise} Promise da função executada
   */
  const execute = async (asyncFn) => {
    loading.value = true
    error.value = null
    try {
      const result = await asyncFn()
      return result
    } catch (err) {
      error.value = err
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Define o estado de loading manualmente
   * @param {boolean} value - Valor do estado de loading
   */
  const setLoading = (value) => {
    loading.value = value
  }

  /**
   * Define o erro manualmente
   * @param {Error|null} err - Erro a ser definido
   */
  const setError = (err) => {
    error.value = err
  }

  /**
   * Limpa o estado de erro
   */
  const clearError = () => {
    error.value = null
  }

  /**
   * Reseta todos os estados
   */
  const reset = () => {
    loading.value = false
    error.value = null
  }

  return {
    loading,
    error,
    execute,
    setLoading,
    setError,
    clearError,
    reset
  }
}

