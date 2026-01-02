<template>
  <div class="process-list">
      <div class="container large">
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
/* Estilos específicos do componente ProcessList */
.process-list {
  padding: 2rem;
}

.card-title {
  word-break: break-word;
}

.info-value {
  word-break: break-word;
}

/* Responsive Styles específicos */
@media (max-width: 768px) {
  .process-list {
    padding: 1rem;
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
}

@media (max-width: 480px) {
  .process-list {
    padding: 0.75rem;
  }

  .filters-container {
    padding: 0.75rem;
  }
}
</style>

