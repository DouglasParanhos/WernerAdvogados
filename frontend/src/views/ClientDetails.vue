<template>
  <div class="client-details">
    <div class="container">
      <div class="header">
        <button @click="goBack" class="btn btn-secondary">← Voltar</button>
        <div class="header-actions">
          <button @click="openClientDocumentModal" class="btn btn-primary">Gerar Documento do Cliente</button>
          <button @click="goToEdit" class="btn btn-secondary">Editar</button>
          <button @click="deleteClient" class="btn btn-danger">Excluir</button>
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
            <button @click="goToNewProcess" class="btn btn-primary">Novo Processo</button>
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
              <div v-for="process in matriculation.processes" :key="process.id" class="process-card">
                <div class="process-header">
                  <strong @click="goToProcessDetails(process.id)" class="process-link">{{ process.numero }}</strong>
                  <div>
                    <button @click.stop="openDocumentModal(process)" class="btn btn-sm btn-primary">Gerar Documento</button>
                    <button @click.stop="goToEditProcess(process.id)" class="btn btn-sm btn-secondary">Editar</button>
                    <button @click.stop="deleteProcess(process.id)" class="btn btn-sm btn-danger">Excluir</button>
                  </div>
                </div>
                <div class="process-info">
                  <div><strong>Comarca:</strong> {{ process.comarca }}</div>
                  <div><strong>Vara:</strong> {{ process.vara }}</div>
                  <div><strong>Sistema:</strong> {{ process.sistema }}</div>
                  <div v-if="process.tipoProcesso"><strong>Tipo:</strong> {{ process.tipoProcesso }}</div>
                  <div v-if="process.status"><strong>Status:</strong> {{ process.status }}</div>
                  <div v-if="process.valor"><strong>Valor:</strong> {{ formatCurrency(process.valor) }}</div>
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
            <button @click="goToNewProcess" class="btn btn-primary">Novo Processo</button>
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
    </div>
  </div>
</template>

<script>
import { personService } from '../services/personService'
import { processService } from '../services/processService'
import DocumentGeneratorModal from '../components/DocumentGeneratorModal.vue'
import ClientDocumentGeneratorModal from '../components/ClientDocumentGeneratorModal.vue'

export default {
  name: 'ClientDetails',
  components: {
    DocumentGeneratorModal,
    ClientDocumentGeneratorModal
  },
  data() {
    return {
      client: null,
      loading: false,
      error: null,
      showDocumentModal: false,
      selectedProcess: null,
      showClientDocumentModal: false
    }
  },
  async mounted() {
    await this.loadClient()
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
    goBack() {
      this.$router.push('/')
    },
    goToEdit() {
      this.$router.push(`/clients/${this.client.id}/edit`)
    },
    goToNewProcess() {
      this.$router.push({ path: '/processes/new', query: { personId: this.client.id } })
    },
    goToProcessDetails(id) {
      this.$router.push(`/processes/${id}`)
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
}

.process-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
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

.no-processes, .empty-state {
  text-align: center;
  padding: 2rem;
  color: #6c757d;
}

/* Estilos de botões importados de styles/buttons.css */

.btn-sm {
  margin-left: 0.5rem;
}

.loading, .error {
  text-align: center;
  padding: 2rem;
  font-size: 1.1rem;
}

.error {
  color: #dc3545;
}
</style>

