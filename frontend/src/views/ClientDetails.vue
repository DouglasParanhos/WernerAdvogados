<template>
  <div class="client-details">
    <div class="container">
      <div class="header">
        <div class="header-left">
          <button @click="goToHome" class="btn-home" title="Voltar para Home">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="9 22 9 12 15 12 15 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <button @click="goBack" class="btn btn-secondary">← Voltar</button>
        </div>
        <div class="header-actions">
          <button @click="openClientDocumentModal" class="icon-btn document-btn" title="Gerar Documento do Cliente">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="14 2 14 8 20 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="16" y1="13" x2="8" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <line x1="16" y1="17" x2="8" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
          <div class="action-buttons">
            <button @click="openLoginModal" class="icon-btn login-btn" title="Configurar Login">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
            <button @click="goToEdit" class="icon-btn edit-btn" title="Editar cliente">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
            <button @click="deleteClient" class="icon-btn delete-btn" title="Excluir cliente">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M3 6h18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
      
      <div v-if="loading" class="loading">Carregando...</div>
      <div v-if="error" class="error">{{ error }}</div>
      
      <div v-if="!loading && !error && client" class="content">
        <div class="section">
          <h2>Dados Pessoais</h2>
          <div class="info-grid">
            <div class="info-item">
              <label>Nome Completo:</label>
              <span>{{ client.fullname }}</span>
            </div>
            <div class="info-item">
              <label>Email:</label>
              <span>{{ client.email }}</span>
            </div>
            <div class="info-item">
              <label>CPF:</label>
              <span>{{ client.cpf }}</span>
            </div>
            <div class="info-item">
              <label>RG:</label>
              <span>{{ client.rg }}</span>
            </div>
            <div class="info-item">
              <label>Estado Civil:</label>
              <span>{{ client.estadoCivil }}</span>
            </div>
            <div class="info-item">
              <label>Data de Nascimento:</label>
              <span>{{ formatDate(client.dataNascimento) }}</span>
            </div>
            <div class="info-item">
              <label>Profissão:</label>
              <span>{{ client.profissao }}</span>
            </div>
            <div class="info-item">
              <label>Telefone:</label>
              <span>{{ client.telefone }}</span>
            </div>
            <div class="info-item">
              <label>Vivo:</label>
              <span>{{ client.vivo ? 'Sim' : 'Não' }}</span>
            </div>
            <div class="info-item" v-if="client.representante">
              <label>Representante:</label>
              <span>{{ client.representante }}</span>
            </div>
            <div class="info-item" v-if="client.idFuncional">
              <label>ID Funcional:</label>
              <span>{{ client.idFuncional }}</span>
            </div>
            <div class="info-item" v-if="client.nacionalidade">
              <label>Nacionalidade:</label>
              <span>{{ client.nacionalidade }}</span>
            </div>
            <div class="info-item login-info">
              <label>Login:</label>
              <div v-if="client.username" class="login-details">
                <span class="username-value">{{ client.username }}</span>
              </div>
              <div v-else class="login-not-configured">
                <span class="no-login-text">Login não configurado</span>
                <button @click="openLoginModal" class="btn btn-sm btn-primary">Configurar Login</button>
              </div>
            </div>
            <div class="info-item login-info" v-if="client.username">
              <label>Senha:</label>
              <div class="password-info">
                <span class="password-status">Senha configurada</span>
                <button @click="regeneratePassword" class="btn btn-sm btn-secondary">Regenerar Senha</button>
              </div>
            </div>
          </div>
        </div>
        
        <div class="section" v-if="client.address">
          <h2>Endereço</h2>
          <div class="info-grid">
            <div class="info-item">
              <label>Logradouro:</label>
              <span>{{ client.address.logradouro }}</span>
            </div>
            <div class="info-item">
              <label>Cidade:</label>
              <span>{{ client.address.cidade }}</span>
            </div>
            <div class="info-item">
              <label>Estado:</label>
              <span>{{ client.address.estado }}</span>
            </div>
            <div class="info-item">
              <label>CEP:</label>
              <span>{{ client.address.cep }}</span>
            </div>
          </div>
        </div>
        
        <div class="section" v-if="client.matriculations && client.matriculations.length > 0">
          <div class="section-header">
            <h2>Matrículas e Processos</h2>
            <button @click="goToNewProcess" class="btn btn-primary btn-new-process-plus" title="Novo Processo">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
          
          <div v-for="matriculation in client.matriculations" :key="matriculation.id" class="matriculation-card">
            <div class="matriculation-header">
              <h3>Matrícula: {{ matriculation.numero }}</h3>
            </div>
            <div class="matriculation-info">
              <div><strong>Cargo:</strong> {{ matriculation.cargo }}</div>
              <div><strong>Início ERJ:</strong> {{ formatDate(matriculation.inicioErj) }}</div>
              <div><strong>Data Aposentadoria:</strong> {{ formatDate(matriculation.dataAposentadoria) }}</div>
              <div><strong>Nível Atual:</strong> {{ matriculation.nivelAtual }}</div>
              <div><strong>Triênio Atual:</strong> {{ matriculation.trienioAtual }}</div>
              <div><strong>Referência:</strong> {{ matriculation.referencia }}</div>
            </div>
            
            <div v-if="matriculation.processes && matriculation.processes.length > 0" class="processes-list">
              <h4>Processos ({{ matriculation.processes.length }})</h4>
              <div 
                v-for="process in matriculation.processes" 
                :key="process.id" 
                class="process-card"
                :class="{ 'archived-process': isArchived(process) }"
              >
                <div class="process-header">
                  <strong @click="goToProcessDetails(process.id)" class="process-link">{{ process.numero }}</strong>
                  <div class="process-actions">
                    <button @click.stop="openDocumentModal(process)" class="icon-btn document-btn" title="Gerar Documento">
                      <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">r
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <polyline points="14 2 14 8 20 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <line x1="16" y1="13" x2="8" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                        <line x1="16" y1="17" x2="8" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                      </svg>
                    </button>
                    <div class="action-buttons">
                      <button @click.stop="goToEditProcess(process.id)" class="icon-btn edit-btn" title="Editar processo">
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                          <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                          <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                      </button>
                      <button @click.stop="deleteProcess(process.id)" class="icon-btn delete-btn" title="Excluir processo">
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                          <path d="M3 6h18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                          <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                      </button>
                    </div>
                  </div>
                </div>
                <div class="process-info">
                  <div><strong>Comarca:</strong> {{ process.comarca }}</div>
                  <div><strong>Vara:</strong> {{ process.vara }}</div>
                  <div><strong>Sistema:</strong> {{ process.sistema }}</div>
                  <div v-if="process.tipoProcesso"><strong>Tipo:</strong> {{ process.tipoProcesso }}</div>
                  <div class="status-field">
                    <strong>Status:</strong>
                    <span v-if="editingStatusId !== process.id" class="status-display">
                      <span v-if="process.status">{{ process.status }}</span>
                      <span v-else class="no-status">Sem status</span>
                      <button 
                        @click.stop="startEditStatus(process)" 
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
                      <button @click.stop="saveStatus(process.id)" class="status-save-btn" title="Salvar">✓</button>
                      <button @click.stop="cancelEditStatus" class="status-cancel-btn" title="Cancelar">✕</button>
                    </div>
                  </div>
                  <div v-if="process.valorOriginal"><strong>Valor Original:</strong> {{ formatCurrency(process.valorOriginal) }}</div>
                  <div v-if="process.valorCorrigido"><strong>Valor Corrigido:</strong> {{ formatCurrency(process.valorCorrigido) }}</div>
                  <div v-if="process.distribuidoEm"><strong>Distribuído em:</strong> {{ formatDate(process.distribuidoEm) }}</div>
                </div>
              </div>
            </div>
            <div v-else class="no-processes">
              Nenhum processo cadastrado para esta matrícula
            </div>
          </div>
        </div>
        
        <div v-else class="section">
          <div class="section-header">
            <h2>Matrículas e Processos</h2>
            <button @click="goToNewProcess" class="btn btn-primary btn-new-process-plus" title="Novo Processo">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
          <p class="empty-state">Nenhuma matrícula cadastrada</p>
        </div>
      </div>
      
      <!-- Modal de Geração de Documentos por Processo -->
      <DocumentGeneratorModal
        :show="showDocumentModal"
        :process="selectedProcess"
        @close="closeDocumentModal"
      />
      
      <!-- Modal de Geração de Documentos do Cliente -->
      <ClientDocumentGeneratorModal
        :show="showClientDocumentModal"
        :client="client"
        @close="closeClientDocumentModal"
      />
      
      <!-- Modal de Configuração de Login -->
      <ClientLoginModal
        :show="showLoginModal"
        :client="client"
        @close="closeLoginModal"
        @saved="handleLoginSaved"
      />
    </div>
  </div>
</template>

<script>
import { personService } from '../services/personService'
import { processService } from '../services/processService'
import DocumentGeneratorModal from '../components/DocumentGeneratorModal.vue'
import ClientDocumentGeneratorModal from '../components/ClientDocumentGeneratorModal.vue'
import ClientLoginModal from '../components/ClientLoginModal.vue'

export default {
  name: 'ClientDetails',
  components: {
    DocumentGeneratorModal,
    ClientDocumentGeneratorModal,
    ClientLoginModal
  },
  data() {
    return {
      client: null,
      loading: false,
      error: null,
      showDocumentModal: false,
      selectedProcess: null,
      showClientDocumentModal: false,
      editingStatusId: null,
      editingStatus: '',
      statusSuggestions: [],
      filteredStatusSuggestions: [],
      showLoginModal: false
    }
  },
  async mounted() {
    await this.loadClient()
    await this.loadStatusSuggestions()
  },
  methods: {
    async loadClient() {
      this.loading = true
      this.error = null
      try {
        const id = this.$route.params.id
        this.client = await personService.getById(id)
      } catch (err) {
        this.error = 'Erro ao carregar cliente: ' + (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.loading = false
      }
    },
    formatDate(date) {
      if (!date) return '-'
      return new Date(date).toLocaleDateString('pt-BR')
    },
    formatCurrency(value) {
      if (!value) return '-'
      return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value)
    },
    goToHome() {
      this.$router.push('/dashboard')
    },
    goBack() {
      this.$router.push('/clients')
    },
    goToEdit() {
      this.$router.push(`/clients/${this.client.id}/edit`)
    },
    goToNewProcess() {
      this.$router.push({ path: '/processes/new', query: { personId: this.client.id } })
    },
    goToProcessDetails(id) {
      this.$router.push({ path: `/processes/${id}`, query: { from: 'client' } })
    },
    goToEditProcess(id) {
      this.$router.push(`/processes/${id}/edit`)
    },
    async deleteClient() {
      if (!confirm('Tem certeza que deseja excluir este cliente?')) {
        return
      }
      try {
        await personService.delete(this.client.id)
        this.$router.push('/')
      } catch (err) {
        alert('Erro ao excluir cliente: ' + (err.response?.data?.message || err.message))
      }
    },
    async deleteProcess(id) {
      if (!confirm('Tem certeza que deseja excluir este processo?')) {
        return
      }
      try {
        await processService.delete(id)
        await this.loadClient()
      } catch (err) {
        alert('Erro ao excluir processo: ' + (err.response?.data?.message || err.message))
      }
    },
    openDocumentModal(process) {
      this.selectedProcess = process
      this.showDocumentModal = true
    },
    closeDocumentModal() {
      this.showDocumentModal = false
      this.selectedProcess = null
    },
    openClientDocumentModal() {
      this.showClientDocumentModal = true
    },
    closeClientDocumentModal() {
      this.showClientDocumentModal = false
    },
    async loadStatusSuggestions() {
      try {
        this.statusSuggestions = await processService.getDistinctStatuses()
        this.filteredStatusSuggestions = this.statusSuggestions
      } catch (err) {
        console.error('Erro ao carregar sugestões de status:', err)
      }
    },
    startEditStatus(process) {
      this.editingStatusId = process.id
      this.editingStatus = process.status || ''
      this.filteredStatusSuggestions = this.statusSuggestions
      this.$nextTick(() => {
        if (this.$refs.statusInput) {
          this.$refs.statusInput.focus()
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
        this.client.matriculations.forEach(mat => {
          if (mat.processes) {
            const index = mat.processes.findIndex(p => p.id === processId)
            if (index !== -1) {
              mat.processes[index] = updatedProcess
            }
          }
        })
        this.cancelEditStatus()
      } catch (err) {
        alert('Erro ao atualizar status: ' + (err.response?.data?.message || err.message))
      }
    },
    cancelEditStatus() {
      this.editingStatusId = null
      this.editingStatus = ''
      this.filteredStatusSuggestions = this.statusSuggestions
    },
    isArchived(process) {
      const status = (process.status || '').toLowerCase()
      return status.includes('arquivado')
    },
    openLoginModal() {
      this.showLoginModal = true
    },
    closeLoginModal() {
      this.showLoginModal = false
    },
    async handleLoginSaved() {
      // Recarregar dados do cliente após salvar credenciais
      await this.loadClient()
    },
    regeneratePassword() {
      // Abrir modal para regenerar senha (a senha será gerada automaticamente)
      this.openLoginModal()
    }
  }
}
</script>

<style scoped>
.client-details {
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
  margin-bottom: 1.5rem;
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

.matriculation-card {
  border: 1px solid #dee2e6;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
  background: #f8f9fa;
}

.matriculation-header {
  margin-bottom: 1rem;
}

.matriculation-header h3 {
  font-size: 1.25rem;
  color: #333;
  margin: 0;
}

.matriculation-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 0.75rem;
  margin-bottom: 1rem;
  font-size: 0.9rem;
}

.processes-list {
  margin-top: 1.5rem;
}

.processes-list h4 {
  font-size: 1.1rem;
  margin-bottom: 1rem;
  color: #495057;
}

.process-card {
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 1rem;
  margin-bottom: 0.75rem;
  transition: opacity 0.2s;
}

.process-card.archived-process {
  opacity: 0.5;
  background-color: #f8f9fa;
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

.login-btn {
  color: #007bff;
}

.login-btn:hover {
  background-color: #f0f8ff;
  color: #0056b3;
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

.document-btn {
  color: #003d7a;
}

.document-btn:hover {
  background-color: #f0f8ff;
  color: #002d5c;
}

.btn-new-process-plus {
  min-width: 40px;
  width: 40px;
  height: 40px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-new-process-plus svg {
  width: 24px;
  height: 24px;
}

.process-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.process-actions {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.process-link {
  cursor: pointer;
  color: #007bff;
  text-decoration: underline;
}

.process-link:hover {
  color: #0056b3;
}

.process-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 0.5rem;
  font-size: 0.9rem;
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

.no-status {
  color: #6c757d;
  font-style: italic;
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
  border-radius: 4px;
  transition: background-color 0.2s;
}

.status-save-btn {
  color: #28a745;
}

.status-save-btn:hover {
  background-color: #f0f9f4;
}

.status-cancel-btn {
  color: #dc3545;
}

.status-cancel-btn:hover {
  background-color: #fff5f5;
}

.no-processes, .empty-state {
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

.login-info {
  grid-column: 1 / -1;
}

.login-details {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.username-value {
  font-weight: 500;
  color: #333;
}

.login-not-configured {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.no-login-text {
  color: #6c757d;
  font-style: italic;
}

.password-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.password-status {
  color: #28a745;
  font-weight: 500;
}

.btn-sm {
  padding: 0.375rem 0.75rem;
  font-size: 0.875rem;
}

/* Responsividade para mobile */
@media (max-width: 768px) {
  .client-details {
    padding: 1rem;
  }

  .header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }

  .header-actions {
    flex-wrap: wrap;
    gap: 0.5rem;
  }

  .section {
    padding: 1rem;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .section-header h2 {
    font-size: 1.25rem;
    margin-bottom: 0;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .matriculation-info {
    grid-template-columns: 1fr;
  }

  .process-info {
    grid-template-columns: 1fr;
  }

  .process-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }

  .process-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .action-buttons {
    flex-wrap: wrap;
  }
}

@media (max-width: 480px) {
  .client-details {
    padding: 0.75rem;
  }

  .section {
    padding: 0.75rem;
  }

  .section h2 {
    font-size: 1.1rem;
  }

  .matriculation-card {
    padding: 1rem;
  }

  .process-card {
    padding: 0.75rem;
  }
}
</style>

