<template>
  <div class="client-list">
    <div class="container">
      <div class="header">
        <h1>Clientes</h1>
        <button @click="goToNewClient" class="btn btn-primary">Novo Cliente</button>
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
                <button @click="goToEditClient(client.id)" class="btn btn-sm btn-secondary">Editar</button>
                <button @click="deleteClient(client.id)" class="btn btn-sm btn-danger">Excluir</button>
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

export default {
  name: 'ClientList',
  data() {
    return {
      clients: [],
      loading: false,
      error: null,
      searchQuery: ''
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
</style>

