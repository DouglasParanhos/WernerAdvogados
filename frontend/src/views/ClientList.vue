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
            placeholder="Buscar cliente por nome..." 
            class="search-input"
          />
        </div>
      </div>
      
      <div v-if="loading" class="loading">Carregando...</div>
      <div v-if="error" class="error">{{ error }}</div>
      
      <div v-if="!loading && !error" class="table-container">
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
              v-for="client in filteredClients" 
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
            <tr v-if="filteredClients.length === 0">
              <td colspan="6" class="empty-state">
                {{ searchQuery ? 'Nenhum cliente encontrado com esse nome' : 'Nenhum cliente encontrado' }}
              </td>
            </tr>
          </tbody>
        </table>
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
      backupLoading: false
    }
  },
  computed: {
    filteredClients() {
      if (!this.searchQuery.trim()) {
        return this.clients
      }
      const query = this.searchQuery.toLowerCase().trim()
      return this.clients.filter(client => {
        const fullname = (client.fullname || '').toLowerCase()
        return fullname.includes(query)
      })
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
        this.clients = await personService.getAll()
      } catch (err) {
        this.error = 'Erro ao carregar clientes: ' + (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.loading = false
      }
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
      this.$router.push('/dashboard')
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

/* Sempre mostrar apenas ícones */
.header-actions .btn-backup,
.header-actions .btn-new-client {
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
</style>

