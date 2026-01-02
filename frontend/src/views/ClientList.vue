<template>
  <div class="client-list">
    <div class="container">
      <div class="header">
        <div class="header-left">
          <button @click="goToHome" class="btn-home" title="Voltar para Home">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="9 22 9 12 15 12 15 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <h1>Clientes</h1>
        </div>
        <div class="header-actions">
          <button @click="showBackupConfirmation" class="btn btn-secondary">Backup do Banco</button>
          <button @click="goToNewClient" class="btn btn-primary">Novo Cliente</button>
        </div>
      </div>
      
      <!-- Modal de Confirmação de Backup -->
      <div v-if="showBackupModal" class="modal-overlay" @click="closeBackupModal">
        <div class="modal-content" @click.stop>
          <h2>Confirmar Backup</h2>
          <p>Deseja realizar o backup do banco de dados?</p>
          <div class="modal-actions">
            <button @click="closeBackupModal" class="btn btn-secondary">Cancelar</button>
            <button @click="performBackup" class="btn btn-primary" :disabled="backupLoading">
              {{ backupLoading ? 'Fazendo backup...' : 'Sim, fazer backup' }}
            </button>
          </div>
        </div>
      </div>
      
      <div v-if="!loading && !error" class="search-container">
        <div class="search-wrapper">
          <span class="search-icon">🔍</span>
          <input 
            type="text" 
            v-model="searchQuery" 
            @input="onSearchChange"
            placeholder="Buscar cliente por nome..." 
            class="search-input"
          />
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
          Mostrando {{ ((currentPage) * pageSize) + 1 }} - {{ Math.min((currentPage + 1) * pageSize, paginationData.totalElements) }} de {{ paginationData.totalElements }} clientes
        </div>
      </div>
      
      <div v-if="!loading && !error" class="table-container">
        <!-- Desktop Table -->
        <table class="client-table">
          <thead>
            <tr>
              <th>Nome</th>
              <th>Email</th>
              <th>CPF</th>
              <th>Telefone</th>
              <th>Processos</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr 
              v-for="client in clients" 
              :key="client.id"
              @click="goToClientDetails(client.id)"
              class="table-row"
            >
              <td>{{ client.fullname }}</td>
              <td>{{ client.email }}</td>
              <td>{{ client.cpf }}</td>
              <td>{{ client.telefone }}</td>
              <td>{{ getProcessCount(client) }}</td>
              <td @click.stop>
                <div class="action-buttons">
                  <button @click="goToEditClient(client.id)" class="icon-btn edit-btn" title="Editar cliente">
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                  </button>
                  <button @click="deleteClient(client.id)" class="icon-btn delete-btn" title="Excluir cliente">
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M3 6h18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="clients.length === 0">
              <td colspan="6" class="empty-state">
                {{ searchQuery ? 'Nenhum cliente encontrado com esse nome' : 'Nenhum cliente encontrado' }}
              </td>
            </tr>
          </tbody>
        </table>
        
        <!-- Mobile Cards -->
        <div class="mobile-cards">
          <div 
            v-for="client in clients" 
            :key="client.id"
            class="mobile-card"
            @click="goToClientDetails(client.id)"
          >
            <div class="card-header">
              <h3 class="card-title">{{ client.fullname }}</h3>
            </div>
            <div class="card-body">
              <div class="card-info">
                <span class="info-label">Email:</span>
                <span class="info-value">{{ client.email }}</span>
              </div>
              <div class="card-info">
                <span class="info-label">CPF:</span>
                <span class="info-value">{{ client.cpf }}</span>
              </div>
              <div class="card-info">
                <span class="info-label">Telefone:</span>
                <span class="info-value">{{ client.telefone }}</span>
              </div>
              <div class="card-info">
                <span class="info-label">Processos:</span>
                <span class="info-value">{{ getProcessCount(client) }}</span>
              </div>
            </div>
            <div class="card-actions" @click.stop>
              <button @click="goToEditClient(client.id)" class="mobile-icon-btn edit-btn" title="Editar cliente">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span>Editar</span>
              </button>
              <button @click="deleteClient(client.id)" class="mobile-icon-btn delete-btn" title="Excluir cliente">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M3 6h18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span>Excluir</span>
              </button>
            </div>
          </div>
          <div v-if="clients.length === 0" class="mobile-empty-state">
            {{ searchQuery ? 'Nenhum cliente encontrado com esse nome' : 'Nenhum cliente encontrado' }}
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
import { personService } from '../services/personService'
import { backupService } from '../services/backupService'

export default {
  name: 'ClientList',
  data() {
    return {
      clients: [],
      loading: false,
      error: null,
      searchQuery: '',
      showBackupModal: false,
      backupLoading: false,
      currentPage: 0,
      pageSize: 10,
      paginationData: null,
      searchTimeout: null
    }
  },
  async mounted() {
    await this.loadClients()
  },
  methods: {
    async loadClients() {
      this.loading = true
      this.error = null
      try {
        const search = this.searchQuery.trim() || null
        const response = await personService.getAll(this.currentPage, this.pageSize, search)
        
        // Verificar se é resposta paginada ou lista simples
        if (response.content) {
          this.clients = response.content
          this.paginationData = {
            totalElements: response.totalElements,
            totalPages: response.totalPages,
            size: response.size,
            number: response.number
          }
        } else {
          // Compatibilidade com resposta não paginada
          this.clients = Array.isArray(response) ? response : []
          this.paginationData = null
        }
      } catch (err) {
        this.error = 'Erro ao carregar clientes: ' + (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.loading = false
      }
    },
    onSearchChange() {
      // Debounce da busca
      if (this.searchTimeout) {
        clearTimeout(this.searchTimeout)
      }
      this.searchTimeout = setTimeout(() => {
        this.currentPage = 0
        this.loadClients()
      }, 500)
    },
    onPageSizeChange() {
      this.currentPage = 0
      this.loadClients()
    },
    goToPage(page) {
      this.currentPage = page
      this.loadClients()
    },
    getProcessCount(client) {
      if (!client.matriculations || client.matriculations.length === 0) {
        return 0
      }
      return client.matriculations.reduce((total, mat) => {
        return total + (mat.processes ? mat.processes.length : 0)
      }, 0)
    },
    goToHome() {
      this.$router.push('/')
    },
    goToClientDetails(id) {
      this.$router.push(`/clients/${id}`)
    },
    goToNewClient() {
      this.$router.push('/clients/new')
    },
    goToEditClient(id) {
      this.$router.push(`/clients/${id}/edit`)
    },
    async deleteClient(id) {
      if (!confirm('Tem certeza que deseja excluir este cliente?')) {
        return
      }
      try {
        await personService.delete(id)
        // Se a página atual ficar vazia após deletar, voltar para página anterior
        if (this.clients.length === 1 && this.currentPage > 0) {
          this.currentPage--
        }
        await this.loadClients()
      } catch (err) {
        alert('Erro ao excluir cliente: ' + (err.response?.data?.message || err.message))
      }
    },
    showBackupConfirmation() {
      this.showBackupModal = true
    },
    closeBackupModal() {
      if (!this.backupLoading) {
        this.showBackupModal = false
      }
    },
    async performBackup() {
      this.backupLoading = true
      try {
        const blob = await backupService.createBackup()
        
        // Criar link para download
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, -5)
        link.href = url
        link.download = `backup_wa_db_${timestamp}.sql`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        
        this.showBackupModal = false
        alert('Backup realizado com sucesso!')
      } catch (err) {
        alert('Erro ao realizar backup: ' + (err.response?.data?.message || err.message))
        console.error(err)
      } finally {
        this.backupLoading = false
      }
    }
  }
}
</script>

<style scoped>
.client-list {
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

.header-actions {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.header h1 {
  font-size: 2rem;
  color: #333;
}

/* Estilos de botões importados de styles/buttons.css */

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  overflow: hidden;
}

.client-table {
  width: 100%;
  border-collapse: collapse;
}

.client-table thead {
  background-color: #f8f9fa;
}

.client-table th {
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #495057;
  border-bottom: 2px solid #dee2e6;
}

.client-table td {
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

.search-container {
  margin-bottom: 2rem;
}

.search-wrapper {
  position: relative;
  display: inline-block;
  width: 100%;
  max-width: 500px;
}

.search-icon {
  position: absolute;
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  font-size: 1.1rem;
  color: #6c757d;
  pointer-events: none;
  z-index: 1;
  transition: color 0.2s;
}

.search-input {
  width: 100%;
  padding: 0.875rem 1rem 0.875rem 3rem;
  font-size: 1rem;
  border: 1.5px solid #e0e0e0;
  border-radius: 8px;
  background-color: #ffffff;
  color: #333;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.search-input:hover {
  border-color: #c0c0c0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.search-input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1), 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-wrapper:focus-within .search-icon {
  color: #007bff;
}

.search-input::placeholder {
  color: #9e9e9e;
  font-style: italic;
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

/* Modal de Confirmação */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  max-width: 400px;
  width: 90%;
}

.modal-content h2 {
  margin-top: 0;
  margin-bottom: 1rem;
  color: #333;
}

.modal-content p {
  margin-bottom: 1.5rem;
  color: #666;
}

.modal-actions {
  display: flex;
  gap: 0.75rem;
  justify-content: flex-end;
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

/* Responsive Styles */
@media (max-width: 768px) {
  .client-list {
    padding: 1rem;
  }

  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .header-actions {
    width: 100%;
    flex-direction: column;
    gap: 0.5rem;
  }

  .header-actions .btn {
    width: 100%;
    min-height: 44px;
  }

  .header h1 {
    font-size: 1.5rem;
  }

  .search-wrapper {
    max-width: 100%;
  }

  .search-input {
    font-size: 16px; /* Prevents zoom on iOS */
    padding: 1rem 1rem 1rem 3rem;
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
  .client-table {
    display: none;
  }

  .mobile-cards {
    display: block;
  }

  .table-container {
    overflow: visible;
  }

  .modal-content {
    padding: 1.5rem;
    margin: 1rem;
  }

  .modal-actions {
    flex-direction: column;
  }

  .modal-actions .btn {
    width: 100%;
    min-height: 44px;
  }
}

@media (max-width: 480px) {
  .client-list {
    padding: 0.75rem;
  }

  .header h1 {
    font-size: 1.25rem;
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
}
</style>

