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
          <button @click="showBackupConfirmation" class="btn btn-secondary btn-backup" title="Backup do Banco">
            <span class="btn-text">Backup do Banco</span>
            <svg class="btn-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <ellipse cx="12" cy="5" rx="9" ry="3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M3 12c0 1.66 4 3 9 3s9-1.34 9-3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M8 12v4M16 12v4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
          <button @click="goToNewClient" class="btn btn-primary btn-new-client" title="Novo Cliente">
            <span class="btn-text">Novo Cliente</span>
            <svg class="btn-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
      </div>
      
      <!-- Modal de Confirmação de Backup -->
      <div v-if="showBackupModal" class="modal-overlay" @click="closeBackupModal">
        <div class="modal-content small" @click.stop>
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
      
      <!-- Modal de Configuração de Login -->
      <ClientLoginModal
        :show="showLoginModal"
        :client="selectedClient"
        @close="closeLoginModal"
        @saved="handleLoginSaved"
      />
      
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
                  <button @click="openLoginModal(client)" class="icon-btn login-btn" title="Configurar Login">
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                  </button>
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
              <button @click="openLoginModal(client)" class="mobile-icon-btn login-btn" title="Configurar Login">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span>Login</span>
              </button>
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
import ClientLoginModal from '../components/ClientLoginModal.vue'

export default {
  name: 'ClientList',
  components: {
    ClientLoginModal
  },
  data() {
    return {
      clients: [],
      loading: false,
      error: null,
      searchQuery: '',
      showBackupModal: false,
      backupLoading: false,
      showLoginModal: false,
      selectedClient: null,
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
    },
    openLoginModal(client) {
      this.selectedClient = client
      this.showLoginModal = true
    },
    closeLoginModal() {
      this.showLoginModal = false
      this.selectedClient = null
    },
    handleLoginSaved() {
      // Recarregar lista de clientes após salvar credenciais
      this.loadClients()
    }
  }
}
</script>

<style scoped>
/* Estilos específicos do componente ClientList */
.client-list {
  padding: 2rem;
}

/* Responsive Styles específicos */
@media (max-width: 768px) {
  .client-list {
    padding: 1rem;
  }
}

@media (max-width: 480px) {
  .client-list {
    padding: 0.75rem;
  }
}
</style>

