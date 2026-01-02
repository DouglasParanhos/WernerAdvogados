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
          <button @click="goToNewProcess" class="btn btn-primary">Novo Processo</button>
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
              @input="onFilterChange"
              placeholder="Buscar por número..."
              class="filter-input"
            />
          </div>
          
          <div class="filter-group">
            <label>Comarca</label>
            <input 
              type="text" 
              v-model="filters.comarca" 
              @input="onFilterChange"
              placeholder="Filtrar por comarca..."
              class="filter-input"
            />
          </div>
          
          <div class="filter-group">
            <label>Vara</label>
            <input 
              type="text" 
              v-model="filters.vara" 
              @input="onFilterChange"
              placeholder="Filtrar por vara..."
              class="filter-input"
            />
          </div>
          
          <div class="filter-group">
            <label>Tema</label>
            <input 
              type="text" 
              v-model="filters.tipoProcesso" 
              @input="onFilterChange"
              placeholder="Filtrar por tema..."
              class="filter-input"
            />
          </div>
          
          <div class="filter-group">
            <label>Status</label>
            <select v-model="filters.status" @change="onFilterChange" class="filter-select">
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
                @change="onFilterChange"
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
      
      <div v-if="!loading && !error" class="pagination-controls-top">
        <div class="pagination-info">
          <span>Mostrar:</span>
          <select v-model="pageSize" @change="onPageSizeChange" class="page-size-select">
            <option :value="10">10</option>
            <option :value="50">50</option>
            <option :value="100">100</option>
          </select>
          <span>registros por página</span>
        </div>
        <div class="pagination-info" v-if="paginationData">
          Mostrando {{ ((currentPage) * pageSize) + 1 }} - {{ Math.min((currentPage + 1) * pageSize, paginationData.totalElements) }} de {{ paginationData.totalElements }} processos
        </div>
      </div>
      
      <div v-if="!loading && !error" class="table-container">
        <!-- Desktop Table -->
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
              v-for="process in processes" 
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
            <tr v-if="processes.length === 0">
              <td colspan="7" class="empty-state">
                {{ hasActiveFilters ? 'Nenhum processo encontrado com os filtros aplicados' : 'Nenhum processo encontrado' }}
              </td>
            </tr>
          </tbody>
        </table>
        
        <!-- Mobile Cards -->
        <div class="mobile-cards">
          <div 
            v-for="process in processes" 
            :key="process.id"
            class="mobile-card"
            @click="goToProcessDetails(process.id)"
          >
            <div class="card-header">
              <h3 class="card-title">{{ process.numero }}</h3>
            </div>
            <div class="card-body">
              <div class="card-info">
                <span class="info-label">Cliente:</span>
                <span class="info-value">{{ getClientName(process) }}</span>
              </div>
              <div class="card-info">
                <span class="info-label">Comarca:</span>
                <span class="info-value">{{ process.comarca }}</span>
              </div>
              <div class="card-info">
                <span class="info-label">Vara:</span>
                <span class="info-value">{{ process.vara }}</span>
              </div>
              <div class="card-info">
                <span class="info-label">Tema:</span>
                <span class="info-value">{{ process.tipoProcesso || '-' }}</span>
              </div>
              <div class="card-info" @click.stop>
                <span class="info-label">Status:</span>
                <div class="mobile-status-field">
                  <span v-if="editingStatusId !== process.id" class="mobile-status-display">
                    <span class="status-badge" :class="getStatusClass(process.status)">
                      {{ process.status || 'Sem status' }}
                    </span>
                    <button 
                      @click="startEditStatus(process)" 
                      class="mobile-status-edit-btn"
                      title="Editar status"
                    >
                      ✏️
                    </button>
                  </span>
                  <div v-else class="mobile-status-edit">
                    <input
                      type="text"
                      v-model="editingStatus"
                      @keyup.enter="saveStatus(process.id)"
                      @keyup.esc="cancelEditStatus"
                      @input="filterStatusSuggestions"
                      class="mobile-status-input"
                      :list="`mobile-status-suggestions-${process.id}`"
                    />
                    <datalist :id="`mobile-status-suggestions-${process.id}`">
                      <option v-for="suggestion in filteredStatusSuggestions" :key="suggestion" :value="suggestion" />
                    </datalist>
                    <button @click="saveStatus(process.id)" class="mobile-status-save-btn" title="Salvar">✓</button>
                    <button @click="cancelEditStatus" class="mobile-status-cancel-btn" title="Cancelar">✕</button>
                  </div>
                </div>
              </div>
            </div>
            <div class="card-actions" @click.stop>
              <button @click="goToEditProcess(process.id)" class="mobile-icon-btn edit-btn" title="Editar processo">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span>Editar</span>
              </button>
              <button @click="deleteProcess(process.id)" class="mobile-icon-btn delete-btn" title="Excluir processo">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M3 6h18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span>Excluir</span>
              </button>
            </div>
          </div>
          <div v-if="processes.length === 0" class="mobile-empty-state">
            {{ hasActiveFilters ? 'Nenhum processo encontrado com os filtros aplicados' : 'Nenhum processo encontrado' }}
          </div>
        </div>
      </div>
      
      <div v-if="!loading && !error && paginationData && paginationData.totalPages > 1" class="pagination-controls">
        <button 
          @click="goToPage(0)" 
          :disabled="currentPage === 0"
          class="pagination-btn"
        >
          Primeira
        </button>
        <button 
          @click="goToPage(currentPage - 1)" 
          :disabled="currentPage === 0"
          class="pagination-btn"
        >
          Anterior
        </button>
        <span class="pagination-page-info">
          Página {{ currentPage + 1 }} de {{ paginationData.totalPages }}
        </span>
        <button 
          @click="goToPage(currentPage + 1)" 
          :disabled="currentPage >= paginationData.totalPages - 1"
          class="pagination-btn"
        >
          Próxima
        </button>
        <button 
          @click="goToPage(paginationData.totalPages - 1)" 
          :disabled="currentPage >= paginationData.totalPages - 1"
          class="pagination-btn"
        >
          Última
        </button>
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
      },
      currentPage: 0,
      pageSize: 10,
      paginationData: null,
      filterTimeout: null
    }
  },
  computed: {
    hasActiveFilters() {
      return Object.values(this.filters).some(value => value !== '') || this.showArchived
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
        // Carregar clientes e statuses (não paginados)
        const [clientsData, statusesData] = await Promise.all([
          personService.getAll(),
          processService.getDistinctStatuses()
        ])
        
        this.clients = Array.isArray(clientsData) ? clientsData : (clientsData.content || [])
        this.distinctStatuses = statusesData
        this.statusSuggestions = statusesData
        this.filteredStatusSuggestions = statusesData
        
        // Criar um mapa de matrículas a partir dos clientes
        this.matriculations = []
        this.clients.forEach(client => {
          if (client.matriculations && client.matriculations.length > 0) {
            client.matriculations.forEach(mat => {
              this.matriculations.push({
                id: mat.id,
                personId: client.id
              })
            })
          }
        })
        
        // Carregar processos com paginação e filtros
        await this.loadProcesses()
      } catch (err) {
        this.error = 'Erro ao carregar processos: ' + (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.loading = false
      }
    },
    async loadProcesses() {
      this.loading = true
      this.error = null
      try {
        const filters = {
          numero: this.filters.numero.trim() || null,
          comarca: this.filters.comarca.trim() || null,
          vara: this.filters.vara.trim() || null,
          tipoProcesso: this.filters.tipoProcesso.trim() || null,
          status: this.filters.status || null,
          showArchived: this.showArchived
        }
        
        const response = await processService.getAll(null, this.currentPage, this.pageSize, filters)
        
        // Verificar se é resposta paginada ou lista simples
        if (response.content) {
          this.processes = response.content
          this.paginationData = {
            totalElements: response.totalElements,
            totalPages: response.totalPages,
            size: response.size,
            number: response.number
          }
        } else {
          // Compatibilidade com resposta não paginada
          this.processes = Array.isArray(response) ? response : []
          this.paginationData = null
        }
      } catch (err) {
        this.error = 'Erro ao carregar processos: ' + (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.loading = false
      }
    },
    onFilterChange() {
      // Debounce dos filtros
      if (this.filterTimeout) {
        clearTimeout(this.filterTimeout)
      }
      this.filterTimeout = setTimeout(() => {
        this.currentPage = 0
        this.loadProcesses()
      }, 500)
    },
    onPageSizeChange() {
      this.currentPage = 0
      this.loadProcesses()
    },
    goToPage(page) {
      this.currentPage = page
      this.loadProcesses()
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
      this.$router.push('/')
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
      this.showArchived = false
      this.currentPage = 0
      this.loadProcesses()
    },
    async deleteProcess(id) {
      if (!confirm('Tem certeza que deseja excluir este processo?')) {
        return
      }
      try {
        await processService.delete(id)
        // Se a página atual ficar vazia após deletar, voltar para página anterior
        if (this.processes.length === 1 && this.currentPage > 0) {
          this.currentPage--
        }
        await this.loadProcesses()
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

.pagination-controls-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.pagination-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: #495057;
}

.page-size-select {
  padding: 0.375rem 0.75rem;
  border: 1.5px solid #e0e0e0;
  border-radius: 6px;
  font-size: 0.9rem;
  background-color: white;
  cursor: pointer;
  transition: all 0.2s;
}

.page-size-select:focus {
  outline: none;
  border-color: #003d7a;
  box-shadow: 0 0 0 3px rgba(0, 61, 122, 0.1);
}

.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1.5rem;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.pagination-btn {
  padding: 0.5rem 1rem;
  border: 1.5px solid #e0e0e0;
  border-radius: 6px;
  background-color: white;
  color: #495057;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.pagination-btn:hover:not(:disabled) {
  background-color: #f8f9fa;
  border-color: #003d7a;
  color: #003d7a;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-page-info {
  padding: 0 1rem;
  font-size: 0.9rem;
  color: #495057;
}

/* Mobile Cards */
.mobile-cards {
  display: none;
}

.mobile-card {
  background: white;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.2s;
}

.mobile-card:active {
  background-color: #f8f9fa;
}

.card-header {
  margin-bottom: 0.75rem;
}

.card-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #333;
  margin: 0;
  word-break: break-word;
}

.card-body {
  margin-bottom: 1rem;
}

.card-info {
  display: flex;
  flex-direction: column;
  margin-bottom: 0.5rem;
}

.info-label {
  font-size: 0.75rem;
  color: #6c757d;
  font-weight: 500;
  margin-bottom: 0.25rem;
}

.info-value {
  font-size: 0.9rem;
  color: #495057;
  word-break: break-word;
}

.mobile-status-field {
  margin-top: 0.25rem;
}

.mobile-status-display {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.mobile-status-edit-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 0.875rem;
  padding: 0.25rem 0.5rem;
  opacity: 0.6;
  transition: opacity 0.2s;
  min-height: 44px;
  min-width: 44px;
}

.mobile-status-edit-btn:active {
  opacity: 1;
}

.mobile-status-edit {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-top: 0.5rem;
}

.mobile-status-input {
  flex: 1;
  min-width: 150px;
  padding: 0.5rem;
  border: 1.5px solid #007bff;
  border-radius: 4px;
  font-size: 0.9rem;
}

.mobile-status-input:focus {
  outline: none;
  border-color: #003d7a;
  box-shadow: 0 0 0 2px rgba(0, 61, 122, 0.1);
}

.mobile-status-save-btn,
.mobile-status-cancel-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  padding: 0.5rem;
  transition: opacity 0.2s;
  min-height: 44px;
  min-width: 44px;
}

.mobile-status-save-btn {
  color: #28a745;
}

.mobile-status-save-btn:active {
  opacity: 0.7;
}

.mobile-status-cancel-btn {
  color: #dc3545;
}

.mobile-status-cancel-btn:active {
  opacity: 0.7;
}

.card-actions {
  display: flex;
  gap: 0.5rem;
  padding-top: 0.75rem;
  border-top: 1px solid #dee2e6;
}

.mobile-icon-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.625rem;
  border: 1.5px solid #e0e0e0;
  border-radius: 6px;
  background-color: white;
  color: #495057;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
  min-height: 44px;
}

.mobile-icon-btn svg {
  width: 18px;
  height: 18px;
}

.mobile-icon-btn.edit-btn {
  color: #6c757d;
}

.mobile-icon-btn.edit-btn:active {
  background-color: #f8f9fa;
  border-color: #003d7a;
  color: #003d7a;
}

.mobile-icon-btn.delete-btn {
  color: #dc3545;
  border-color: #dc3545;
}

.mobile-icon-btn.delete-btn:active {
  background-color: #fff5f5;
  border-color: #c82333;
  color: #c82333;
}

.mobile-empty-state {
  text-align: center;
  padding: 2rem;
  color: #6c757d;
  background: white;
  border-radius: 8px;
}

/* Responsividade */
@media (max-width: 768px) {
  .process-list {
    padding: 1rem;
  }

  .container {
    max-width: 100%;
  }

  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .header-actions {
    width: 100%;
  }

  .header-actions .btn {
    width: 100%;
    min-height: 44px;
  }

  .header h1 {
    font-size: 1.5rem;
  }

  .filters-container {
    padding: 1rem;
  }

  .filters-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .filter-input,
  .filter-select {
    font-size: 16px; /* Prevents zoom on iOS */
    padding: 0.75rem;
    min-height: 44px;
  }

  .filter-actions {
    justify-content: stretch;
  }

  .filter-actions .btn {
    width: 100%;
    min-height: 44px;
  }

  .pagination-controls-top {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .pagination-info {
    flex-wrap: wrap;
    font-size: 0.85rem;
  }

  .pagination-info span:last-child {
    display: none; /* Hide long text on mobile */
  }

  .pagination-controls {
    flex-wrap: wrap;
    gap: 0.5rem;
  }

  .pagination-btn {
    min-height: 44px;
    flex: 1;
    min-width: calc(50% - 0.25rem);
  }

  .pagination-page-info {
    width: 100%;
    text-align: center;
    padding: 0.5rem 0;
    order: -1;
  }

  /* Hide table, show cards */
  .process-table {
    display: none;
  }

  .mobile-cards {
    display: block;
  }

  .table-container {
    overflow: visible;
  }
}

@media (max-width: 480px) {
  .process-list {
    padding: 0.75rem;
  }

  .header h1 {
    font-size: 1.25rem;
  }

  .filters-container {
    padding: 0.75rem;
  }

  .pagination-controls {
    gap: 0.375rem;
  }

  .pagination-btn {
    font-size: 0.8rem;
    padding: 0.5rem 0.75rem;
  }

  /* Hide "Primeira" and "Última" buttons on very small screens */
  .pagination-btn:first-child,
  .pagination-btn:last-child {
    display: none;
  }

  .card-title {
    font-size: 1rem;
  }

  .info-value {
    font-size: 0.85rem;
  }

  .mobile-status-edit {
    flex-direction: column;
    align-items: stretch;
  }

  .mobile-status-input {
    width: 100%;
  }
}
</style>

