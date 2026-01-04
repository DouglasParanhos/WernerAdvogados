<template>
  <div class="novaescola-calculation">
    <div class="calculation-container">
      <div class="calculation-header">
        <div class="header-left">
          <button @click="goToHome" class="btn-home" title="Voltar para Home">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="9 22 9 12 15 12 15 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <button @click="goBack" class="back-button">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            Voltar
          </button>
        </div>
        <h1>Cálculo NOVAESCOLA</h1>
        <p class="subtitle">Gere uma planilha Excel detalhada com correção monetária e juros de mora</p>
      </div>
      
      <div class="calculation-form">
        <div class="form-group">
          <label for="grade">Nota da Escola</label>
          <select id="grade" v-model.number="grade" @change="updateBaseValue" class="form-control">
            <option :value="null">Selecione uma nota</option>
            <option :value="1">1</option>
            <option :value="2">2</option>
            <option :value="3">3</option>
            <option :value="4">4</option>
            <option :value="5">5</option>
          </select>
          <small class="form-help">A nota será multiplicada por 100 para obter o valor base das parcelas</small>
        </div>
        
        <div class="form-group">
          <label for="baseValue">Valor Base da Parcela (R$)</label>
          <select id="baseValue" v-model.number="baseValue" class="form-control">
            <option :value="100">R$ 100,00</option>
            <option :value="200">R$ 200,00</option>
            <option :value="300">R$ 300,00</option>
            <option :value="400">R$ 400,00</option>
            <option :value="500">R$ 500,00</option>
          </select>
          <small class="form-help">Você pode ajustar manualmente o valor base se necessário</small>
        </div>
        
          <!-- Fator IPCA-E -->
          <div class="form-group">
            <label>Fator de Correção IPCA-E</label>
            <div class="factor-input-group">
              <input
                type="text"
                value="2.88680560"
                readonly
                class="form-control factor-input"
              />
              <small class="form-help">Acumulado desde 02/2003 até 30/11/2021</small>
            </div>
          </div>
        
        <!-- Fator SELIC -->
        <div class="form-group">
          <label>Fator de Correção SELIC</label>
          <div class="factor-input-group">
            <button 
              @click="updateSelicFactor" 
              :disabled="loadingSelic"
              class="refresh-button"
              title="Atualizar fator SELIC"
            >
              <svg v-if="!loadingSelic" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M1 4v6h6M23 20v-6h-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M20.49 9A9 9 0 0 0 5.64 5.64L1 10m22 4l-4.64 4.36A9 9 0 0 1 3.51 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span v-else class="spinner"></span>
            </button>
            <input 
              type="text" 
              :value="selicFactor || 'Clique no botão para calcular'" 
              readonly 
              class="form-control factor-input"
            />
            <small class="form-help">Acumulado desde 09/12/2021 até hoje</small>
          </div>
        </div>
        
        <div class="form-actions">
          <button 
            @click="generateCalculation" 
            :disabled="!canGenerate || loading" 
            class="btn-primary"
          >
            <span v-if="loading">Gerando...</span>
            <span v-else>Gerar Cálculo e Planilha Excel</span>
          </button>
        </div>
        
        <div v-if="error" class="error-message">
          {{ error }}
        </div>
        
        <div v-if="success" class="success-message">
          Planilha gerada com sucesso! O download começará automaticamente.
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { calculationService } from '../services/calculationService'

export default {
  name: 'NovaEscolaCalculation',
  data() {
    return {
      grade: null,
      baseValue: 100,
      loading: false,
      error: null,
      success: false,
      ipcaEFactor: null,
      selicFactor: null,
      loadingIpcaE: false,
      loadingSelic: false
    }
  },
  computed: {
    canGenerate() {
      return this.baseValue > 0
    }
  },
  methods: {
    goToHome() {
      this.$router.push('/dashboard')
    },
    goBack() {
      this.$router.push('/calculations')
    },
    updateBaseValue() {
      if (this.grade && this.grade >= 1 && this.grade <= 5) {
        this.baseValue = this.grade * 100
      }
    },
    async generateCalculation() {
      if (!this.canGenerate) {
        this.error = 'Por favor, selecione um valor base válido'
        return
      }
      
      // Verificar se os fatores estão disponíveis
      if (!this.selicFactor || this.selicFactor === 'Clique no botão para calcular' || this.selicFactor === 'Erro ao calcular') {
        this.error = 'Por favor, atualize o fator SELIC antes de gerar o cálculo'
        return
      }
      
      this.loading = true
      this.error = null
      this.success = false
      
      try {
        // Converter os fatores para números
        const ipcaEFactor = parseFloat(this.ipcaEFactor || '2.88680560')
        const selicFactor = parseFloat(this.selicFactor)
        
        await calculationService.calculateNovaEscola(
          this.grade, 
          this.baseValue,
          ipcaEFactor,
          selicFactor
        )
        this.success = true
        setTimeout(() => {
          this.success = false
        }, 5000)
      } catch (err) {
        this.error = err.response?.data?.message || err.message || 'Erro ao gerar cálculo. Tente novamente.'
        console.error('Erro ao gerar cálculo:', err)
      } finally {
        this.loading = false
      }
    },
    async updateSelicFactor() {
      this.loadingSelic = true
      try {
        const result = await calculationService.getSelicFactor('09/12/2021')
        this.selicFactor = result.fator ? parseFloat(result.fator).toFixed(8) : null
      } catch (err) {
        console.error('Erro ao buscar fator SELIC:', err)
        this.selicFactor = 'Erro ao calcular'
      } finally {
        this.loadingSelic = false
      }
    }
  }
}
</script>

<style scoped>
.novaescola-calculation {
  min-height: 100vh;
  background: linear-gradient(135deg, #d0d8e0 0%, #e8eef5 50%, #c0c8d0 100%);
  background-attachment: fixed;
  padding: 2rem;
}

.calculation-container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 16px;
  padding: 2.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.calculation-header {
  margin-bottom: 2rem;
  position: relative;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.btn-home {
  background: transparent;
  border: 1.5px solid #6c757d;
  border-radius: 8px;
  padding: 0.5rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6c757d;
  transition: all 0.2s;
  width: 40px;
  height: 40px;
}

.btn-home:hover {
  background-color: #f8f9fa;
  color: #003d7a;
  border-color: #003d7a;
}

.btn-home svg {
  width: 20px;
  height: 20px;
}

.back-button {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: transparent;
  border: 1px solid #003d7a;
  color: #003d7a;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
  margin: 0;
  height: 40px;
}

.back-button:hover {
  background: #003d7a;
  color: white;
}

.back-button svg {
  width: 16px;
  height: 16px;
}

.calculation-header h1 {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.subtitle {
  font-size: 1.1rem;
  color: #4a5568;
  font-weight: 400;
}

.calculation-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 600;
  color: #2d3748;
  font-size: 1rem;
}

.form-control {
  padding: 0.75rem;
  border: 1px solid #cbd5e0;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.form-control:focus {
  outline: none;
  border-color: #003d7a;
  box-shadow: 0 0 0 3px rgba(0, 61, 122, 0.1);
}

.form-help {
  color: #718096;
  font-size: 0.875rem;
}

.form-actions {
  margin-top: 1rem;
}

.btn-primary {
  width: 100%;
  padding: 1rem 2rem;
  background: linear-gradient(135deg, #003d7a 0%, #5a7ba8 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 61, 122, 0.3);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 61, 122, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.error-message {
  padding: 1rem;
  background: #fee;
  border: 1px solid #fcc;
  border-radius: 8px;
  color: #c33;
  margin-top: 1rem;
}

.success-message {
  padding: 1rem;
  background: #efe;
  border: 1px solid #cfc;
  border-radius: 8px;
  color: #3c3;
  margin-top: 1rem;
}

.factor-input-group {
  display: flex;
  gap: 0.5rem;
  align-items: flex-start;
}

.refresh-button {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.75rem;
  background: linear-gradient(135deg, #003d7a 0%, #5a7ba8 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 48px;
  height: 48px;
  flex-shrink: 0;
}

.refresh-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 61, 122, 0.4);
}

.refresh-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.refresh-button svg {
  width: 20px;
  height: 20px;
}

.factor-input {
  flex: 1;
  background-color: #f7fafc;
  cursor: default;
}

.spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .calculation-container {
    padding: 1.5rem;
  }
  
  .calculation-header h1 {
    font-size: 2rem;
  }
  
  .subtitle {
    font-size: 1rem;
  }
}
</style>

