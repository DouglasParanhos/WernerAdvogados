<template>
  <div class="tasks-board">
    <div class="container">
      <div class="header">
        <div class="header-left">
          <button @click="goToHome" class="btn-home" title="Voltar para Home">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="9 22 9 12 15 12 15 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <h1>Tarefas</h1>
        </div>
        <div class="header-actions">
          <div class="filter-group">
            <label for="filter-responsavel">Filtrar por:</label>
            <select id="filter-responsavel" v-model="filterResponsavel" class="filter-select">
              <option value="">Todos</option>
              <option value="Liz">Liz</option>
              <option value="Angelo">Angelo</option>
              <option value="Thiago">Thiago</option>
            </select>
          </div>
          <button @click="exportToExcel" class="btn-export" title="Exportar para Excel">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="14 2 14 8 20 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="16" y1="13" x2="8" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="16" y1="17" x2="8" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="10 9 9 9 8 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            Exportar Excel
          </button>
          <button @click="showNewTaskModal = true" class="btn-icon-add" title="Nova Tarefa">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
              <path d="M12 8v8M8 12h8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>
      </div>
      
      <div v-if="loading" class="loading">Carregando...</div>
      <div v-if="error" class="error">{{ error }}</div>
      
      <div v-if="!loading && !error" class="board-container">
        <div 
          v-for="column in columns" 
          :key="column.status" 
          class="board-column"
          @drop="onDrop($event, column.status)"
          @dragover.prevent
          @dragenter.prevent
        >
          <div class="column-header" :style="{ backgroundColor: column.color }">
            <h2>{{ column.title }}</h2>
            <span class="task-count">{{ getTasksByStatus(column.status).length }}</span>
          </div>
          
          <div class="tasks-list">
            <div
              v-for="task in getTasksByStatus(column.status)"
              :key="task.id"
              class="task-card"
              :class="getTaskColorClass(task.tipoTarefa)"
              :draggable="true"
              @dragstart="onDragStart($event, task)"
              @click="openEditTaskModal(task)"
            >
              <div class="task-header">
                <span class="task-type-badge" :style="{ backgroundColor: getTaskTypeColor(task.tipoTarefa) }">
                  {{ getTaskTypeLabel(task.tipoTarefa) }}
                </span>
                <button @click.stop="deleteTask(task.id)" class="task-delete-btn" title="Excluir tarefa">
                  ×
                </button>
              </div>
              <h3 class="task-title">{{ task.titulo }}</h3>
              <p v-if="task.descricao" class="task-description">{{ task.descricao }}</p>
              <div class="task-footer">
                <span class="task-responsavel">{{ task.responsavel }}</span>
              </div>
            </div>
            
            <!-- Botão Carregar Mais para tarefas COMPLETA -->
            <div v-if="column.status === 'COMPLETA' && hasMoreCompleted() && !showAllCompleted" class="load-more-container">
              <button @click="showAllCompleted = true" class="btn-load-more">
                Carregar Mais
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Modal Nova/Editar Tarefa -->
      <div v-if="showNewTaskModal || showEditTaskModal" class="modal-overlay" @click="closeModal">
        <div class="modal-content" @click.stop>
          <h2>{{ showEditTaskModal ? 'Editar Tarefa' : 'Nova Tarefa' }}</h2>
          <form @submit.prevent="saveTask">
            <div class="form-group">
              <label>Título *</label>
              <input v-model="taskForm.titulo" type="text" required />
            </div>
            
            <div class="form-group">
              <label>Descrição</label>
              <textarea v-model="taskForm.descricao" rows="3"></textarea>
            </div>
            
            <div class="form-group">
              <label>Tipo de Tarefa *</label>
              <select v-model="taskForm.tipoTarefa" required>
                <option value="">Selecione...</option>
                <option value="PRESENCIAL">Presencial / Diligência</option>
                <option value="COMUNICAR_CLIENTE">Comunicar com Cliente</option>
                <option value="ESCRITA_PECA">Escrita de Peça</option>
                <option value="PRAZO">Prazo</option>
                <option value="ADMINISTRATIVO">Administrativo</option>
              </select>
            </div>
            
            <div class="form-group">
              <label>Status *</label>
              <select v-model="taskForm.status" required>
                <option value="PARA_INICIAR">Para Iniciar</option>
                <option value="EM_ANDAMENTO">Em Andamento</option>
                <option value="COMPLETA">Completa</option>
              </select>
            </div>
            
            <div class="form-group">
              <label>Responsável *</label>
              <select v-model="taskForm.responsavel" required>
                <option value="">Selecione...</option>
                <option value="Liz">Liz</option>
                <option value="Angelo">Angelo</option>
                <option value="Thiago">Thiago</option>
              </select>
            </div>
            
            <div class="modal-actions">
              <button type="button" @click="closeModal" class="btn btn-secondary">Cancelar</button>
              <button type="submit" class="btn btn-primary">Salvar</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { taskService } from '../services/taskService'

export default {
  name: 'Tasks',
  data() {
    return {
      tasks: [],
      loading: false,
      error: null,
      showNewTaskModal: false,
      showEditTaskModal: false,
      draggedTask: null,
      filterResponsavel: '',
      showAllCompleted: false,
      taskForm: {
        id: null,
        titulo: '',
        descricao: '',
        tipoTarefa: '',
        status: 'PARA_INICIAR',
        responsavel: ''
      },
      columns: [
        { status: 'PARA_INICIAR', title: 'Para Iniciar', color: '#e2e8f0' },
        { status: 'EM_ANDAMENTO', title: 'Em Andamento', color: '#dbeafe' },
        { status: 'COMPLETA', title: 'Completa', color: '#d1fae5' }
      ]
    }
  },
  async mounted() {
    await this.loadTasks()
  },
  methods: {
    async loadTasks() {
      this.loading = true
      this.error = null
      try {
        this.tasks = await taskService.getAll()
      } catch (err) {
        this.error = 'Erro ao carregar tarefas: ' + (err.response?.data?.message || err.message)
        console.error(err)
      } finally {
        this.loading = false
      }
    },
    getTasksByStatus(status) {
      let filtered = this.tasks
        .filter(task => {
          const matchesStatus = task.status === status
          const matchesFilter = !this.filterResponsavel || task.responsavel === this.filterResponsavel
          return matchesStatus && matchesFilter
        })
      
      // Para tarefas COMPLETA, ordenar por data de modificação (mais recentes primeiro)
      if (status === 'COMPLETA') {
        filtered = filtered.sort((a, b) => {
          const dateA = new Date(a.modifiedOn || a.createdOn || 0)
          const dateB = new Date(b.modifiedOn || b.createdOn || 0)
          return dateB - dateA // Mais recentes primeiro
        })
        
        // Limitar a 10 se não estiver mostrando todas
        if (!this.showAllCompleted) {
          filtered = filtered.slice(0, 10)
        }
      } else {
        // Para outras colunas, ordenar: PRAZO primeiro, depois por ordem
        filtered = filtered.sort((a, b) => {
          // Tarefas PRAZO sempre no topo
          const aIsPrazo = a.tipoTarefa === 'PRAZO'
          const bIsPrazo = b.tipoTarefa === 'PRAZO'
          
          if (aIsPrazo && !bIsPrazo) return -1
          if (!aIsPrazo && bIsPrazo) return 1
          
          // Se ambas são PRAZO ou nenhuma é, ordenar por ordem
          return (a.ordem || 0) - (b.ordem || 0)
        })
      }
      
      return filtered
    },
    hasMoreCompleted() {
      const completed = this.tasks.filter(task => {
        const matchesStatus = task.status === 'COMPLETA'
        const matchesFilter = !this.filterResponsavel || task.responsavel === this.filterResponsavel
        return matchesStatus && matchesFilter
      })
      return completed.length > 10 && !this.showAllCompleted
    },
    getTaskTypeColor(tipo) {
      const colors = {
        PRESENCIAL: '#3b82f6', // Azul
        COMUNICAR_CLIENTE: '#f97316', // Laranja
        ESCRITA_PECA: '#ef4444', // Vermelho
        PRAZO: '#ef4444', // Vermelho
        ADMINISTRATIVO: '#10b981' // Verde
      }
      return colors[tipo] || '#6b7280'
    },
    getTaskTypeLabel(tipo) {
      const labels = {
        PRESENCIAL: 'Presencial',
        COMUNICAR_CLIENTE: 'Comunicar',
        ESCRITA_PECA: 'Escrita',
        PRAZO: 'Prazo',
        ADMINISTRATIVO: 'Administrativo'
      }
      return labels[tipo] || tipo
    },
    getTaskColorClass(tipo) {
      return `task-${tipo.toLowerCase()}`
    },
    onDragStart(event, task) {
      this.draggedTask = task
      event.dataTransfer.effectAllowed = 'move'
    },
    async onDrop(event, newStatus) {
      event.preventDefault()
      if (!this.draggedTask) return
      
      if (this.draggedTask.status === newStatus) {
        this.draggedTask = null
        return
      }
      
      try {
        const tasksInNewStatus = this.getTasksByStatus(newStatus)
        const newOrder = tasksInNewStatus.length + 1
        
        await taskService.update(this.draggedTask.id, {
          ...this.draggedTask,
          status: newStatus,
          ordem: newOrder
        })
        
        await this.loadTasks()
      } catch (err) {
        alert('Erro ao mover tarefa: ' + (err.response?.data?.message || err.message))
      } finally {
        this.draggedTask = null
      }
    },
    openEditTaskModal(task) {
      this.taskForm = {
        id: task.id,
        titulo: task.titulo,
        descricao: task.descricao || '',
        tipoTarefa: task.tipoTarefa,
        status: task.status,
        responsavel: task.responsavel
      }
      this.showEditTaskModal = true
    },
    closeModal() {
      this.showNewTaskModal = false
      this.showEditTaskModal = false
      this.taskForm = {
        id: null,
        titulo: '',
        descricao: '',
        tipoTarefa: '',
        status: 'PARA_INICIAR',
        responsavel: ''
      }
    },
    async saveTask() {
      try {
        if (this.taskForm.id) {
          await taskService.update(this.taskForm.id, this.taskForm)
        } else {
          await taskService.create(this.taskForm)
        }
        await this.loadTasks()
        this.closeModal()
      } catch (err) {
        alert('Erro ao salvar tarefa: ' + (err.response?.data?.message || err.message))
      }
    },
    async deleteTask(id) {
      if (!confirm('Tem certeza que deseja excluir esta tarefa?')) {
        return
      }
      try {
        await taskService.delete(id)
        await this.loadTasks()
      } catch (err) {
        alert('Erro ao excluir tarefa: ' + (err.response?.data?.message || err.message))
      }
    },
    goToHome() {
      this.$router.push('/')
    },
    exportToExcel() {
      try {
        if (!this.tasks || this.tasks.length === 0) {
          alert('Não há tarefas para exportar.')
          return
        }
        
        // Preparar dados para exportação
        const data = this.tasks.map(task => ({
          'Título': task.titulo || '',
          'Descrição': task.descricao || '',
          'Tipo': this.getTaskTypeLabel(task.tipoTarefa),
          'Status': task.status === 'PARA_INICIAR' ? 'Para Iniciar' : 
                   task.status === 'EM_ANDAMENTO' ? 'Em Andamento' : 'Completa',
          'Responsável': task.responsavel || '',
          'Criado em': task.createdOn ? new Date(task.createdOn).toLocaleString('pt-BR') : '',
          'Modificado em': task.modifiedOn ? new Date(task.modifiedOn).toLocaleString('pt-BR') : ''
        }))
        
        // Criar CSV
        const headers = Object.keys(data[0])
        const csvContent = [
          headers.join(','),
          ...data.map(row => 
            headers.map(header => {
              const value = row[header] || ''
              // Escapar vírgulas e aspas
              return `"${String(value).replace(/"/g, '""')}"`
            }).join(',')
          )
        ].join('\n')
        
        // Adicionar BOM para Excel reconhecer UTF-8
        const BOM = '\uFEFF'
        const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' })
        
        // Criar link para download
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, -5)
        link.href = url
        link.download = `tarefas_${timestamp}.csv`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      } catch (err) {
        alert('Erro ao exportar tarefas: ' + err.message)
        console.error(err)
      }
    }
  }
}
</script>

<style scoped>
.tasks-board {
  padding: 2rem;
  min-height: 100vh;
  background: #f5f5f5;
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
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #4a5568;
  transition: color 0.2s;
}

.btn-home:hover {
  color: #003d7a;
}

.btn-home svg {
  width: 24px;
  height: 24px;
}

h1 {
  font-size: 2rem;
  color: #1a1a1a;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter-group label {
  font-weight: 600;
  color: #4a5568;
  font-size: 0.875rem;
}

.filter-select {
  padding: 0.5rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  background: white;
  color: #1a1a1a;
  cursor: pointer;
  transition: border-color 0.2s;
}

.filter-select:hover {
  border-color: #003d7a;
}

.filter-select:focus {
  outline: none;
  border-color: #003d7a;
  box-shadow: 0 0 0 3px rgba(0, 61, 122, 0.1);
}

.btn-icon-add {
  background: #003d7a;
  color: white;
  border: none;
  border-radius: 50%;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(0, 61, 122, 0.2);
}

.btn-icon-add:hover {
  background: #002d5a;
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgba(0, 61, 122, 0.3);
}

.btn-icon-add:active {
  transform: scale(0.95);
}

.btn-icon-add svg {
  width: 24px;
  height: 24px;
}

.btn-export {
  background: #10b981;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.875rem;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(16, 185, 129, 0.2);
}

.btn-export:hover {
  background: #059669;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(16, 185, 129, 0.3);
}

.btn-export:active {
  transform: translateY(0);
}

.btn-export svg {
  width: 18px;
  height: 18px;
}

.board-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
  margin-top: 2rem;
}

.board-column {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  min-height: 500px;
  display: flex;
  flex-direction: column;
}

.column-header {
  padding: 1rem;
  border-radius: 8px 8px 0 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.column-header h2 {
  margin: 0;
  font-size: 1.25rem;
  color: #1a1a1a;
  font-weight: 600;
}

.task-count {
  background: rgba(0,0,0,0.1);
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 600;
}

.tasks-list {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
  min-height: 400px;
}

.task-card {
  background: white;
  border-radius: 6px;
  padding: 1rem;
  margin-bottom: 0.75rem;
  cursor: pointer;
  transition: all 0.2s;
  border-left: 4px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.task-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0,0,0,0.15);
}

.task-card.task-presencial {
  border-left-color: #3b82f6;
}

.task-card.task-comunicar_cliente {
  border-left-color: #f97316;
}

.task-card.task-escrita_peca,
.task-card.task-prazo {
  border-left-color: #ef4444;
}

.task-card.task-administrativo {
  border-left-color: #10b981;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.task-type-badge {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  color: white;
}

.task-delete-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 1.5rem;
  color: #9ca3af;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.task-delete-btn:hover {
  color: #ef4444;
}

.task-title {
  font-size: 1rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 0.5rem 0;
}

.task-description {
  font-size: 0.875rem;
  color: #6b7280;
  margin: 0 0 0.75rem 0;
  line-height: 1.4;
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-responsavel {
  font-size: 0.875rem;
  color: #4a5568;
  font-weight: 500;
}

.load-more-container {
  padding: 1rem;
  text-align: center;
}

.btn-load-more {
  background: #e2e8f0;
  color: #4a5568;
  border: 1px solid #cbd5e0;
  border-radius: 6px;
  padding: 0.75rem 1.5rem;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  width: 100%;
}

.btn-load-more:hover {
  background: #cbd5e0;
  border-color: #a0aec0;
  color: #2d3748;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content h2 {
  margin: 0 0 1.5rem 0;
  color: #1a1a1a;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #4a5568;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #003d7a;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 2rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary {
  background: #003d7a;
  color: white;
}

.btn-primary:hover {
  background: #002d5a;
}

.btn-secondary {
  background: #e2e8f0;
  color: #4a5568;
}

.btn-secondary:hover {
  background: #cbd5e0;
}

.loading, .error {
  text-align: center;
  padding: 2rem;
  font-size: 1.1rem;
}

.error {
  color: #ef4444;
}

@media (max-width: 1024px) {
  .board-container {
    grid-template-columns: 1fr;
  }
}
</style>

