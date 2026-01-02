<template>
  <div class="process-list">
    <div class="container">
      <div class="header">
        <div class="header-left">
          <button @click="goToHome" class="btn-home" title="Voltar para Home">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="9 22 9 12 15 12 15 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <h1>Processos</h1>
        </div>
        <div class="header-actions">
          <button @click="goToNewProcess" class="btn btn-primary btn-new-process" title="Novo Processo">
            <span class="btn-text">Novo Processo</span>
            <svg class="btn-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
      </div>
      
      <!-- Filtros -->
      <div v-if="!loading && !error" class="filters-container">
        <div class="filters-grid">
          <div class="filter-group">
            <label>Número do Processo</label>
            <input 
              type="text" 
              v-model="filters.numero" 
              placeholder="Buscar por número..."
              class="filter-input"
            />
          </div>
          
          <div class="filter-group">
            <label>Comarca</label>
            <input 
              type="text" 
              v-model="filters.comarca" 
              placeholder="Filtrar por comarca..."
              class="filter-input"
            />
          </div>
          
          <div class="filter-group">
            <label>Vara</label>
            <input 
              type="text" 
              v-model="filters.vara" 
              placeholder="Filtrar por vara..."
              class="filter-input"
            />
          </div>
          
          <div class="filter-group">
            <label>Tema</label>
            <input 
              type="text" 
              v-model="filters.tipoProcesso" 
              placeholder="Filtrar por tema..."
              class="filter-input"
            />
          </div>
          
          <div class="filter-group">
            <label>Status</label>
            <select v-model="filters.status" class="filter-select">
              <option value="">Todos</option>
              <option v-for="status in distinctStatuses" :key="status" :value="status">
                {{ status }}
              </option>
            </select>
          </div>
          
          <div class="filter-group filter-checkbox">
            <label class="checkbox-label">
              <input 
                type="checkbox" 
                v-model="showArchived"
                class="checkbox-input"
              />
              <span>Mostrar processos arquivados</span>
            </label>
          </div>
          
          <div class="filter-group filter-actions">
            <button @click="clearFilters" class="btn btn-secondary">Limpar Filtros</button>
          </div>
        </div>
      </div>
      
      <div v-if="loading" class="loading">Carregando...</div>
      <div v-if="error" class="error">{{ error }}</div>
      
      <div v-if="!loading && !error" class="table-container">
        <table class="process-table">
          <thead>
            <tr>
              <th>Número do Processo</th>
              <th>Nome do Cliente</th>
              <th>Comarca</th>
              <th>Vara</th>
              <th>Tema</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr 
              v-for="process in filteredProcesses" 
              :key="process.id"
              @click="goToProcessDetails(process.id)"
              class="table-row"
            >
              <td>{{ process.numero }}</td>
              <td>{{ getClientName(process) }}</td>
              <td>{{ process.comarca }}</td>
              <td>{{ process.vara }}</td>
              <td>{{ process.tipoProcesso || '-' }}</td>
              <td @click.stop>
                <div class="status-field">
                  <span v-if="editingStatusId !== process.id" class="status-display">
                    <span class="status-badge" :class="getStatusClass(process.status)">
                      {{ process.status || 'Sem status' }}
                    </span>
                    <button 
                      @click="startEditStatus(process)" 
                      class="status-edit-btn"
                      title="Editar status"
                    >
                      ✏️
                    </button>
                  </span>
                  <div v-else class="status-edit">
                    <input
                      type="text"
                      v-model="editingStatus"
                      @keyup.enter="saveStatus(process.id)"
                      @keyup.esc="cancelEditStatus"
                      @input="filterStatusSuggestions"
                      class="status-input"
                      :list="`status-suggestions-${process.id}`"
                      ref="statusInput"
                    />
                    <datalist :id="`status-suggestions-${process.id}`">
                      <option v-for="suggestion in filteredStatusSuggestions" :key="suggestion" :value="suggestion" />
                    </datalist>
                    <button @click="saveStatus(process.id)" class="status-save-btn" title="Salvar">✓</button>
                    <button @click="cancelEditStatus" class="status-cancel-btn" title="Cancelar">✕</button>
                  </div>
                </div>
              </td>
              <td @click.stop>
                <div class="action-buttons">
                  <button @click="goToEditProcess(process.id)" class="icon-btn edit-btn" title="Editar processo">
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                  </button>
                  <button @click="deleteProcess(process.id)" class="icon-btn delete-btn" title="Excluir processo">
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M3 6h18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="filteredProcesses.length === 0">
              <td colspan="7" class="empty-state">
                {{ hasActiveFilters ? 'Nenhum processo encontrado com os filtros aplicados' : 'Nenhum processo encontrado' }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import { processService } from '../services/processService'
import { personService } from '../services/personService'

export default {
  name: 'ProcessList',
  data() {
    return {
      processes: [],
      clients: [],
      matriculations: [],
      loading: false,
      error: null,
      distinctStatuses: [],
      editingStatusId: null,
      editingStatus: '',
      statusSuggestions: [],
      filteredStatusSuggestions: [],
      showArchived: false,
      filters: {
        numero: '',
        comarca: '',
        vara: '',
        tipoProcesso: '',
        status: ''
      }
    }
  },
  computed: {
    filteredProcesses() {
      let filtered = this.processes

      // Filtrar processos arquivados se a checkbox não estiver marcada
      if (!this.showArchived) {
        filtered = filtered.filter(p => {
          const status = (p.status || '').toLowerCase()
          return !status.includes('arquivado')
        })
      }

      if (this.filters.numero) {
        const numero = this.filters.numero.toLowerCase().trim()
        filtered = filtered.filter(p => 
          (p.numero || '').toLowerCase().includes(numero)
        )
      }

      if (this.filters.comarca) {
        const comarca = this.filters.comarca.toLowerCase().trim()
        filtered = filtered.filter(p => 
          (p.comarca || '').toLowerCase().includes(comarca)
        )
      }

      if (this.filters.vara) {
        const vara = this.filters.vara.toLowerCase().trim()
        filtered = filtered.filter(p => 
          (p.vara || '').toLowerCase().includes(vara)
        )
      }

      if (this.filters.tipoProcesso) {
        const tipo = this.filters.tipoProcesso.toLowerCase().trim()
        filtered = filtered.filter(p => 
          (p.tipoProcesso || '').toLowerCase().includes(tipo)
        )
      }

      if (this.filters.status) {
        filtered = filtered.filter(p => 
          (p.status || '') === this.filters.status
        )
      }

      return filtered
    },
    hasActiveFilters() {
      return Object.values(this.filters).some(value => value !== '')
    }
  },
  async mounted() {
    await this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      this.error = null
      try {
        // Carregar processos, clientes e statuses em paralelo
        const [processesData, clientsData, statusesData] = await Promise.all([
          processService.getAll(),
          personService.getAll(),
          processService.getDistinctStatuses()
        ])
        
        this.processes = processesData
        this.clients = clientsData
        this.distinctStatuses = statusesData
        this.statusSuggestions = statusesData
        this.filteredStatusSuggestions = statusesData
        
        // Criar um mapa de matrículas a partir dos clientes
        this.matriculations = []
        clientsData.forEach(client => {
          if (client.matriculations && client.matriculations.length > 0) {
            client.matriculations.forEach(mat => {
              this.matriculations.push({
                id: mat.id,
                personId: client.id
              })
            })
          }
        })
      } catch (err) {
        this.error = 'Erro ao carregar processos: ' + (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.loading = false
      }
    },
    getClientName(process) {
      if (!process.matriculationId) return '-'
      
      // Encontrar a matrícula do processo
      const matriculation = this.matriculations.find(m => m.id === process.matriculationId)
      if (!matriculation) return '-'
      
      // Encontrar o cliente da matrícula
      const client = this.clients.find(c => c.id === matriculation.personId)
      return client ? client.fullname : '-'
    },
    getStatusClass(status) {
      if (!status) return 'status-none'
      const statusLower = status.toLowerCase()
      if (statusLower.includes('concluído') || statusLower.includes('arquivado')) {
        return 'status-success'
      }
      if (statusLower.includes('suspenso') || statusLower.includes('aguardando')) {
        return 'status-warning'
      }
      if (statusLower.includes('cancelado') || statusLower.includes('extinto')) {
        return 'status-danger'
      }
      return 'status-info'
    },
    goToHome() {
      this.$router.push('/dashboard')
    },
    goToProcessDetails(id) {
      this.$router.push(`/processes/${id}`)
    },
    goToNewProcess() {
      this.$router.push('/processes/new')
    },
    goToEditProcess(id) {
      this.$router.push(`/processes/${id}/edit`)
    },
    clearFilters() {
      this.filters = {
        numero: '',
        comarca: '',
        vara: '',
        tipoProcesso: '',
        status: ''
      }
    },
    async deleteProcess(id) {
      if (!confirm('Tem certeza que deseja excluir este processo?')) {
        return
      }
      try {
        await processService.delete(id)
        await this.loadData()
      } catch (err) {
        alert('Erro ao excluir processo: ' + (err.response?.data?.message || err.message))
      }
    },
    startEditStatus(process) {
      this.editingStatusId = process.id
      this.editingStatus = process.status || ''
      this.filteredStatusSuggestions = this.statusSuggestions
      this.$nextTick(() => {
        const inputs = this.$refs.statusInput
        if (inputs) {
          const input = Array.isArray(inputs) ? inputs[0] : inputs
          if (input) {
            input.focus()
          }
        }
      })
    },
    filterStatusSuggestions() {
      const query = this.editingStatus.toLowerCase()
      if (!query) {
        this.filteredStatusSuggestions = this.statusSuggestions
      } else {
        this.filteredStatusSuggestions = this.statusSuggestions.filter(status =>
          status.toLowerCase().includes(query)
        )
      }
    },
    async saveStatus(processId) {
      try {
        const updatedProcess = await processService.updateStatus(processId, this.editingStatus)
        // Atualizar o processo na lista
        const index = this.processes.findIndex(p => p.id === processId)
        if (index !== -1) {
          this.processes[index] = updatedProcess
        }
        this.cancelEditStatus()
      } catch (err) {
        alert('Erro ao atualizar status: ' + (err.response?.data?.message || err.message))
      }
    },
    cancelEditStatus() {
      this.editingStatusId = null
      this.editingStatus = ''
      this.filteredStatusSuggestions = this.statusSuggestions
    }
  }
}
</script>

<style scoped>
.process-list {
  padding: 2rem;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1rem;
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

.header h1 {
  font-size: 2rem;
  color: #333;
}

.header-actions {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.filters-container {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.filters-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  align-items: end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.filter-group label {
  font-size: 0.875rem;
  font-weight: 600;
  color: #495057;
}

.filter-input,
.filter-select {
  padding: 0.625rem;
  border: 1.5px solid #e0e0e0;
  border-radius: 6px;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.filter-input:focus,
.filter-select:focus {
  outline: none;
  border-color: #003d7a;
  box-shadow: 0 0 0 3px rgba(0, 61, 122, 0.1);
}

.filter-checkbox {
  justify-content: flex-start;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  font-size: 0.9rem;
  color: #495057;
  user-select: none;
}

.checkbox-input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #003d7a;
}

.filter-actions {
  justify-content: flex-end;
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  overflow: hidden;
}

.process-table {
  width: 100%;
  border-collapse: collapse;
}

.process-table thead {
  background-color: #f8f9fa;
}

.process-table th {
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #495057;
  border-bottom: 2px solid #dee2e6;
}

.process-table td {
  padding: 1rem;
  border-bottom: 1px solid #dee2e6;
}

.table-row {
  cursor: pointer;
  transition: background-color 0.2s;
}

.table-row:hover {
  background-color: #f8f9fa;
}

.empty-state {
  text-align: center;
  padding: 2rem;
  color: #6c757d;
}

.loading, .error {
  text-align: center;
  padding: 2rem;
  font-size: 1.1rem;
}

.error {
  color: #dc3545;
}

.status-field {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.status-display {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.status-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 500;
}

.status-edit-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 0.875rem;
  padding: 0.25rem 0.5rem;
  opacity: 0.6;
  transition: opacity 0.2s;
}

.status-edit-btn:hover {
  opacity: 1;
}

.status-edit {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
}

.status-input {
  flex: 1;
  padding: 0.375rem 0.5rem;
  border: 1.5px solid #007bff;
  border-radius: 4px;
  font-size: 0.9rem;
  min-width: 150px;
}

.status-input:focus {
  outline: none;
  border-color: #003d7a;
  box-shadow: 0 0 0 2px rgba(0, 61, 122, 0.1);
}

.status-save-btn,
.status-cancel-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  padding: 0.25rem 0.5rem;
  transition: opacity 0.2s;
}

.status-save-btn {
  color: #28a745;
}

.status-save-btn:hover {
  opacity: 0.7;
}

.status-cancel-btn {
  color: #dc3545;
}

.status-cancel-btn:hover {
  opacity: 0.7;
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

.status-none {
  background-color: #e9ecef;
  color: #6c757d;
}

.status-success {
  background-color: #d4edda;
  color: #155724;
}

.status-warning {
  background-color: #fff3cd;
  color: #856404;
}

.status-danger {
  background-color: #f8d7da;
  color: #721c24;
}

.status-info {
  background-color: #d1ecf1;
  color: #0c5460;
}

/* Sempre mostrar apenas ícones */
.header-actions .btn-new-process {
  padding: 0.75rem;
  min-width: auto;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-actions .btn-text {
  display: none;
}

.header-actions .btn-icon {
  display: block;
  width: 24px;
  height: 24px;
}

/* Responsividade */
@media (max-width: 768px) {
  .filters-grid {
    grid-template-columns: 1fr;
  }
  
  .process-table {
    font-size: 0.875rem;
  }
  
  .process-table th,
  .process-table td {
    padding: 0.75rem 0.5rem;
  }
}
</style>

