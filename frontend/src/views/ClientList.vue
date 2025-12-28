<template>
  <div class="client-list">
    <div class="container">
      <div class="header">
        <h1>Clientes</h1>
        <button @click="goToNewClient" class="btn btn-primary">Novo Cliente</button>
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
                <button @click="goToEditClient(client.id)" class="btn btn-sm btn-secondary">Editar</button>
                <button @click="deleteClient(client.id)" class="btn btn-sm btn-danger">Excluir</button>
              </td>
            </tr>
            <tr v-if="clients.length === 0">
              <td colspan="6" class="empty-state">Nenhum cliente encontrado</td>
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
      error: null
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

.btn-primary:hover {
  background-color: #0056b3;
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
  margin-right: 0.5rem;
}

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
</style>

