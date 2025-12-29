<template>
  <div class="process-details">
    <div class="container">
      <div class="header">
        <button @click="goBack" class="btn btn-secondary">← Voltar</button>
        <div class="header-actions">
          <div class="action-buttons">
            <button @click="goToEdit" class="icon-btn edit-btn" title="Editar processo">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
      
      <div v-if="loading" class="loading">Carregando...</div>
      <div v-if="error" class="error">{{ error }}</div>
      
      <div v-if="!loading && !error && process" class="content">
        <!-- Dados do Processo -->
        <div class="section">
          <h2>Dados do Processo</h2>
          <div class="info-grid">
            <div class="info-item">
              <label>Número:</label>
              <span>{{ process.numero }}</span>
            </div>
            <div class="info-item">
              <label>Comarca:</label>
              <span>{{ process.comarca }}</span>
            </div>
            <div class="info-item">
              <label>Vara:</label>
              <span>{{ process.vara }}</span>
            </div>
            <div class="info-item">
              <label>Sistema:</label>
              <span>{{ process.sistema }}</span>
            </div>
            <div class="info-item" v-if="process.tipoProcesso">
              <label>Tipo:</label>
              <span>{{ process.tipoProcesso }}</span>
            </div>
            <div class="info-item" v-if="process.status">
              <label>Status:</label>
              <span>{{ process.status }}</span>
            </div>
            <div class="info-item" v-if="process.valor">
              <label>Valor:</label>
              <span>{{ formatCurrency(process.valor) }}</span>
            </div>
            <div class="info-item" v-if="process.previsaoHonorariosContratuais">
              <label>Previsão Honorários Contratuais:</label>
              <span>{{ formatCurrency(process.previsaoHonorariosContratuais) }}</span>
            </div>
            <div class="info-item" v-if="process.previsaoHonorariosSucumbenciais">
              <label>Previsão Honorários Sucumbenciais:</label>
              <span>{{ formatCurrency(process.previsaoHonorariosSucumbenciais) }}</span>
            </div>
            <div class="info-item" v-if="process.distribuidoEm">
              <label>Distribuído em:</label>
              <span>{{ formatDate(process.distribuidoEm) }}</span>
            </div>
          </div>
        </div>
        
        <!-- Movimentações -->
        <div class="section">
          <div class="section-header">
            <h2>Movimentações</h2>
            <button @click="showNewMovimentForm = true" class="btn btn-primary">Nova Movimentação</button>
          </div>
          
          <!-- Formulário de Nova Movimentação -->
          <div v-if="showNewMovimentForm" class="moviment-form">
            <h3>Nova Movimentação</h3>
            <div class="form-group">
              <label>Data:</label>
              <input 
                type="text" 
                v-model="newMoviment.date" 
                class="form-control"
                placeholder="dd/mm/aaaa"
                @input="formatDateInput($event, 'newMoviment.date')"
                maxlength="10"
              />
            </div>
            <div class="form-group">
              <label>Descrição:</label>
              <textarea v-model="newMoviment.descricao" class="form-control" rows="3"></textarea>
            </div>
            <div class="form-actions">
              <button @click="saveNewMoviment" class="btn btn-primary" :disabled="saving">Salvar</button>
              <button @click="cancelNewMoviment" class="btn btn-secondary">Cancelar</button>
            </div>
          </div>
          
          <!-- Lista de Movimentações -->
          <div v-if="moviments && moviments.length > 0" class="moviments-list">
            <div v-for="moviment in moviments" :key="moviment.id" class="moviment-card">
              <div v-if="editingMovimentId !== moviment.id" class="moviment-display">
                <div class="moviment-header">
                  <div class="moviment-date">{{ formatDate(moviment.date) }}</div>
                  <div class="moviment-actions">
                    <div class="action-buttons">
                      <button @click="startEditMoviment(moviment)" class="icon-btn edit-btn" title="Editar movimentação">
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                          <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                          <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                      </button>
                      <button @click="deleteMoviment(moviment.id)" class="icon-btn delete-btn" title="Excluir movimentação">
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                          <path d="M3 6h18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                          <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                      </button>
                    </div>
                  </div>
                </div>
                <div class="moviment-description">{{ moviment.descricao }}</div>
              </div>
              
              <!-- Formulário de Edição -->
              <div v-else class="moviment-edit-form">
                <div class="form-group">
                  <label>Data:</label>
                  <input 
                    type="text" 
                    v-model="editingMoviment.date" 
                    class="form-control"
                    placeholder="dd/mm/aaaa"
                    @input="formatDateInput($event, 'editingMoviment.date')"
                    maxlength="10"
                  />
                </div>
                <div class="form-group">
                  <label>Descrição:</label>
                  <textarea v-model="editingMoviment.descricao" class="form-control" rows="3"></textarea>
                </div>
                <div class="form-actions">
                  <button @click="saveEditMoviment" class="btn btn-primary" :disabled="saving">Salvar</button>
                  <button @click="cancelEditMoviment" class="btn btn-secondary">Cancelar</button>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="no-moviments">
            Nenhuma movimentação cadastrada
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { processService } from '../services/processService'
import { movimentService } from '../services/movimentService'

export default {
  name: 'ProcessDetails',
  data() {
    return {
      process: null,
      moviments: [],
      loading: false,
      error: null,
      showNewMovimentForm: false,
      editingMovimentId: null,
      editingMoviment: null,
      saving: false,
      newMoviment: {
        descricao: '',
        date: '',
        processId: null
      }
    }
  },
  async mounted() {
    await this.loadProcess()
    await this.loadMoviments()
  },
  methods: {
    async loadProcess() {
      this.loading = true
      this.error = null
      try {
        const id = this.$route.params.id
        this.process = await processService.getById(id)
        this.newMoviment.processId = id
      } catch (err) {
        this.error = 'Erro ao carregar processo: ' + (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.loading = false
      }
    },
    async loadMoviments() {
      try {
        const processId = this.$route.params.id
        this.moviments = await movimentService.getAll(processId)
      } catch (err) {
        console.error('Erro ao carregar movimentações:', err)
      }
    },
    formatDate(date) {
      if (!date) return '-'
      const d = new Date(date)
      const day = String(d.getDate()).padStart(2, '0')
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const year = d.getFullYear()
      return `${day}/${month}/${year}`
    },
    formatCurrency(value) {
      if (!value) return '-'
      return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value)
    },
    goBack() {
      this.$router.back()
    },
    goToEdit() {
      this.$router.push(`/processes/${this.process.id}/edit`)
    },
    formatDateInput(event, fieldPath) {
      // Aplica máscara dd/mm/aaaa enquanto o usuário digita
      let value = event.target.value.replace(/\D/g, '') // Remove tudo que não é dígito
      
      if (value.length > 2) {
        value = value.substring(0, 2) + '/' + value.substring(2)
      }
      if (value.length > 5) {
        value = value.substring(0, 5) + '/' + value.substring(5, 9)
      }
      
      // Atualiza o modelo Vue baseado no caminho do campo
      const parts = fieldPath.split('.')
      if (parts.length === 2) {
        // Ex: newMoviment.date
        if (!this[parts[0]]) {
          this[parts[0]] = {}
        }
        this.$set(this[parts[0]], parts[1], value)
      } else if (parts.length === 3) {
        // Ex: editingMoviment.date
        if (!this[parts[0]]) {
          this[parts[0]] = {}
        }
        if (!this[parts[0]][parts[1]]) {
          this.$set(this[parts[0]], parts[1], {})
        }
        this.$set(this[parts[0]][parts[1]], parts[2], value)
      }
      
      // Atualiza o valor no campo
      event.target.value = value
    },
    convertDateToISO(dateString) {
      // Converte de dd/mm/aaaa para ISO (yyyy-MM-dd)
      if (!dateString) return null
      
      // Remove espaços e caracteres especiais
      const cleanDate = dateString.trim()
      
      // Verifica se está no formato dd/mm/aaaa
      const datePattern = /^(\d{2})\/(\d{2})\/(\d{4})$/
      const match = cleanDate.match(datePattern)
      
      if (match) {
        const day = match[1]
        const month = match[2]
        const year = match[3]
        
        // Valida a data
        const date = new Date(`${year}-${month}-${day}`)
        if (!isNaN(date.getTime()) && 
            date.getDate() == day && 
            date.getMonth() + 1 == month && 
            date.getFullYear() == year) {
          // Retorna no formato yyyy-MM-dd (sem hora)
          return `${year}-${month}-${day}`
        }
      }
      
      // Se não estiver no formato esperado, tenta parsear como está
      console.warn('Data em formato inválido:', dateString)
      return null
    },
    formatDateForInput(dateString) {
      // Converte de formato ISO (yyyy-MM-dd ou yyyy-MM-ddTHH:mm:ss) para dd/mm/aaaa
      if (!dateString) return ''
      try {
        let date
        if (typeof dateString === 'string') {
          // Remove a parte de hora se existir
          const dateOnly = dateString.split('T')[0].split(' ')[0]
          const parts = dateOnly.split('-')
          if (parts.length === 3) {
            // Formato yyyy-MM-dd
            return `${parts[2]}/${parts[1]}/${parts[0]}`
          }
          // Tenta parsear como data ISO
          date = new Date(dateString)
        } else {
          date = new Date(dateString)
        }
        
        if (date && !isNaN(date.getTime())) {
          const day = String(date.getDate()).padStart(2, '0')
          const month = String(date.getMonth() + 1).padStart(2, '0')
          const year = date.getFullYear()
          return `${day}/${month}/${year}`
        }
        return ''
      } catch (error) {
        console.error('Erro ao formatar data:', dateString, error)
        return ''
      }
    },
    async saveNewMoviment() {
      if (!this.newMoviment.descricao || !this.newMoviment.date) {
        alert('Por favor, preencha todos os campos')
        return
      }
      
      this.saving = true
      try {
        // Converter de dd/mm/aaaa para formato ISO
        const dateISO = this.convertDateToISO(this.newMoviment.date)
        if (!dateISO) {
          alert('Data inválida. Use o formato dd/mm/aaaa')
          this.saving = false
          return
        }
        await movimentService.create({
          ...this.newMoviment,
          date: dateISO
        })
        await this.loadMoviments()
        this.cancelNewMoviment()
      } catch (err) {
        alert('Erro ao salvar movimentação: ' + (err.response?.data?.message || err.message))
      } finally {
        this.saving = false
      }
    },
    cancelNewMoviment() {
      this.showNewMovimentForm = false
      this.newMoviment = {
        descricao: '',
        date: '',
        processId: this.process.id
      }
    },
    startEditMoviment(moviment) {
      this.editingMovimentId = moviment.id
      // Converter data para formato dd/mm/aaaa
      this.editingMoviment = {
        id: moviment.id,
        descricao: moviment.descricao,
        date: this.formatDateForInput(moviment.date),
        processId: moviment.processId
      }
    },
    async saveEditMoviment() {
      if (!this.editingMoviment.descricao || !this.editingMoviment.date) {
        alert('Por favor, preencha todos os campos')
        return
      }
      
      this.saving = true
      try {
        // Converter de dd/mm/aaaa para formato ISO
        const dateISO = this.convertDateToISO(this.editingMoviment.date)
        if (!dateISO) {
          alert('Data inválida. Use o formato dd/mm/aaaa')
          this.saving = false
          return
        }
        await movimentService.update(this.editingMoviment.id, {
          ...this.editingMoviment,
          date: dateISO
        })
        await this.loadMoviments()
        this.cancelEditMoviment()
      } catch (err) {
        alert('Erro ao salvar movimentação: ' + (err.response?.data?.message || err.message))
      } finally {
        this.saving = false
      }
    },
    cancelEditMoviment() {
      this.editingMovimentId = null
      this.editingMoviment = null
    },
    async deleteMoviment(id) {
      if (!confirm('Tem certeza que deseja excluir esta movimentação?')) {
        return
      }
      try {
        await movimentService.delete(id)
        await this.loadMoviments()
      } catch (err) {
        alert('Erro ao excluir movimentação: ' + (err.response?.data?.message || err.message))
      }
    }
  }
}
</script>

<style scoped>
.process-details {
  padding: 2rem;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.header-actions {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 2rem;
  margin-bottom: 2rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.section h2 {
  font-size: 1.5rem;
  color: #333;
  margin: 0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1rem;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.info-item label {
  font-weight: 600;
  color: #6c757d;
  font-size: 0.875rem;
}

.info-item span {
  color: #333;
  font-size: 1rem;
}

.moviment-form {
  background: #f8f9fa;
  border-radius: 6px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
}

.moviment-form h3 {
  margin-top: 0;
  margin-bottom: 1rem;
  color: #333;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #495057;
}

.form-control {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 1rem;
}

.form-control:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0,123,255,0.25);
}

.form-actions {
  display: flex;
  gap: 0.5rem;
}

.moviments-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.moviment-card {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 1rem;
}

.moviment-display {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.moviment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.moviment-date {
  font-weight: 600;
  color: #495057;
  font-size: 0.9rem;
}

.moviment-description {
  color: #333;
  line-height: 1.5;
}

.moviment-actions {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.icon-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0.375rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  border-radius: 4px;
}

.icon-btn svg {
  width: 18px;
  height: 18px;
}

.edit-btn {
  color: #6c757d;
}

.edit-btn:hover {
  background-color: #f8f9fa;
  color: #495057;
}

.delete-btn {
  color: #dc3545;
}

.delete-btn:hover {
  background-color: #fff5f5;
  color: #c82333;
}

.moviment-edit-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.no-moviments {
  text-align: center;
  padding: 2rem;
  color: #6c757d;
}

/* Estilos de botões importados de styles/buttons.css */

.loading, .error {
  text-align: center;
  padding: 2rem;
  font-size: 1.1rem;
}

.error {
  color: #dc3545;
}
</style>

