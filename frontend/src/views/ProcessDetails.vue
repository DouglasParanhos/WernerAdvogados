<template>
  <div class="process-details">
    <div class="container">
      <div class="header">
        <button @click="goBack" class="btn btn-secondary">← Voltar</button>
        <div>
          <button @click="goToEdit" class="btn btn-secondary">Editar Processo</button>
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
            <div class="info-item" v-if="process.valorOriginal">
              <label>Valor Original:</label>
              <span>{{ formatCurrency(process.valorOriginal) }}</span>
            </div>
            <div class="info-item" v-if="process.valorCorrigido">
              <label>Valor Corrigido:</label>
              <span>{{ formatCurrency(process.valorCorrigido) }}</span>
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
              <input type="datetime-local" v-model="newMoviment.date" class="form-control" />
            </div>
            <div class="form-group">
              <label>Descrição:</label>
              <textarea v-model="newMoviment.descricao" class="form-control" rows="3"></textarea>
            </div>
            <div class="form-group checkbox-group">
              <label class="checkbox-label">
                <input type="checkbox" v-model="newMoviment.visibleToClient" class="checkbox-input" />
                <span class="checkbox-custom"></span>
                <span class="checkbox-text">Visível para o cliente</span>
              </label>
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
                    <button @click="startEditMoviment(moviment)" class="btn btn-sm btn-secondary">Editar</button>
                    <button @click="deleteMoviment(moviment.id)" class="btn btn-sm btn-danger">Excluir</button>
                  </div>
                </div>
                <div class="moviment-description">{{ moviment.descricao }}</div>
              </div>
              
              <!-- Formulário de Edição -->
              <div v-else class="moviment-edit-form">
                <div class="form-group">
                  <label>Data:</label>
                  <input type="datetime-local" v-model="editingMoviment.date" class="form-control" />
                </div>
                <div class="form-group">
                  <label>Descrição:</label>
                  <textarea v-model="editingMoviment.descricao" class="form-control" rows="3"></textarea>
                </div>
                <div class="form-group checkbox-group">
                  <label class="checkbox-label">
                    <input type="checkbox" v-model="editingMoviment.visibleToClient" class="checkbox-input" />
                    <span class="checkbox-custom"></span>
                    <span class="checkbox-text">Visível para o cliente</span>
                  </label>
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
import { matriculationService } from '../services/matriculationService'

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
        processId: null,
        visibleToClient: true
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
      return d.toLocaleString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    formatCurrency(value) {
      if (!value) return '-'
      return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value)
    },
    async goBack() {
      // Voltar para o cliente se o processo tem matrícula com personId, senão para lista de processos
      if (this.process?.matriculationId) {
        try {
          const matriculation = await matriculationService.getById(this.process.matriculationId)
          if (matriculation?.personId) {
            this.$router.push(`/clients/${matriculation.personId}`)
            return
          }
        } catch (err) {
          console.error('Erro ao buscar matrícula:', err)
        }
      }
      // Se não conseguir encontrar o cliente, voltar para lista de processos
      this.$router.push('/processes')
    },
    goToEdit() {
      this.$router.push(`/processes/${this.process.id}/edit`)
    },
    async saveNewMoviment() {
      if (!this.newMoviment.descricao || !this.newMoviment.date) {
        alert('Por favor, preencha todos os campos')
        return
      }
      
      this.saving = true
      try {
        // Converter para formato ISO
        const dateISO = new Date(this.newMoviment.date).toISOString()
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
        processId: this.process.id,
        visibleToClient: true
      }
    },
    startEditMoviment(moviment) {
      this.editingMovimentId = moviment.id
      // Converter data para formato datetime-local
      const date = new Date(moviment.date)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      this.editingMoviment = {
        id: moviment.id,
        descricao: moviment.descricao,
        date: `${year}-${month}-${day}T${hours}:${minutes}`,
        processId: moviment.processId,
        visibleToClient: moviment.visibleToClient !== undefined ? moviment.visibleToClient : true
      }
    },
    async saveEditMoviment() {
      if (!this.editingMoviment.descricao || !this.editingMoviment.date) {
        alert('Por favor, preencha todos os campos')
        return
      }
      
      this.saving = true
      try {
        // Converter para formato ISO
        const dateISO = new Date(this.editingMoviment.date).toISOString()
        // Remover o id do objeto antes de enviar, pois ele já está na URL
        const { id, ...movimentData } = this.editingMoviment
        await movimentService.update(this.editingMoviment.id, {
          ...movimentData,
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

/* Checkbox customizado */
.checkbox-group {
  margin-bottom: 1.5rem;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
  margin-bottom: 0 !important;
  font-weight: 500;
  color: #495057;
}

.checkbox-input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkbox-custom {
  position: relative;
  display: inline-block;
  width: 20px;
  height: 20px;
  min-width: 20px;
  min-height: 20px;
  background-color: #fff;
  border: 2px solid #ced4da;
  border-radius: 4px;
  margin-right: 0.75rem;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.checkbox-label:hover .checkbox-custom {
  border-color: #007bff;
  background-color: #f0f7ff;
}

.checkbox-input:checked ~ .checkbox-custom {
  background-color: #007bff;
  border-color: #007bff;
}

.checkbox-input:checked ~ .checkbox-custom::after {
  content: '';
  position: absolute;
  left: 6px;
  top: 2px;
  width: 5px;
  height: 10px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.checkbox-input:focus ~ .checkbox-custom {
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25);
}

.checkbox-text {
  font-size: 0.95rem;
  line-height: 1.5;
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

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #0056b3;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background-color: #545b62;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-danger:hover {
  background-color: #c82333;
}

.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}

.loading, .error {
  text-align: center;
  padding: 2rem;
  font-size: 1.1rem;
}

.error {
  color: #dc3545;
}

/* Responsividade para mobile */
@media (max-width: 768px) {
  .process-details {
    padding: 1rem;
  }

  .container {
    max-width: 100%;
  }

  .header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }

  .header > div {
    display: flex;
    gap: 0.5rem;
  }

  .section {
    padding: 1rem;
    margin-bottom: 1rem;
  }

  .section-header {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
  }

  .section-header h2 {
    font-size: 1.25rem;
  }

  .section-header button {
    width: 100%;
  }

  .info-grid {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }

  .moviment-form {
    padding: 1rem;
  }

  .moviment-form h3 {
    font-size: 1.1rem;
  }

  .form-group {
    margin-bottom: 1rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions button {
    width: 100%;
  }

  .moviment-card {
    padding: 0.75rem;
  }

  .moviment-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }

  .moviment-actions {
    width: 100%;
    justify-content: stretch;
  }

  .moviment-actions button {
    flex: 1;
  }

  .moviment-edit-form {
    gap: 0.75rem;
  }

  .btn {
    padding: 0.625rem 1rem;
    font-size: 0.9rem;
  }

  .btn-sm {
    padding: 0.5rem 0.75rem;
    font-size: 0.8rem;
  }

  .checkbox-label {
    font-size: 0.9rem;
  }

  .checkbox-custom {
    width: 18px;
    height: 18px;
    min-width: 18px;
    min-height: 18px;
    margin-right: 0.5rem;
  }

  .checkbox-input:checked ~ .checkbox-custom::after {
    left: 5px;
    top: 1px;
    width: 4px;
    height: 8px;
  }
}

@media (max-width: 480px) {
  .process-details {
    padding: 0.5rem;
  }

  .section {
    padding: 0.75rem;
  }

  .section h2 {
    font-size: 1.1rem;
  }

  .moviment-form h3 {
    font-size: 1rem;
  }

  .form-control {
    font-size: 0.9rem;
    padding: 0.625rem;
  }

  .checkbox-text {
    font-size: 0.85rem;
  }
}
</style>

